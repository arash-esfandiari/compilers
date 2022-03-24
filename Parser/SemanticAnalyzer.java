import absyn.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class SemanticAnalyzer implements AbsynVisitor {

    final static int SPACES = 4;
    public HashMap<String, ArrayList<NodeType>> table;

    public SemanticAnalyzer() {
        table = new HashMap<String, ArrayList<NodeType>>();
    }

    private void writeToFile(String string, boolean nextLine) {
        try {
            FileWriter myWriter = new FileWriter("symbolTable.sym", true);
            if (nextLine) {
                myWriter.write(string + "\n");
            } else
                myWriter.write(string);
            myWriter.close();
        } catch (IOException e) {
            System.out.println("FileWriter unsuccessful");
            e.printStackTrace();
        }
    }

    private void indent(int level) {
        for (int i = 0; i < level * SPACES; i++)
            writeToFile(" ", false);
    }

    public boolean isInteger(Dec dtype) {
        if (dtype.typ.typ == 1) {
            return true;
        }
        return false;
    }

    /*
     * Function: Inserts NodeType in the table HashMap ArrayList
     * Param: NodeType to be intsered
     * returns: void
     */
    public void insert(NodeType nType) {
        if (table.containsKey(nType.name)) {
            ArrayList<NodeType> list = table.get(nType.name);
            list.add(nType);
        } else {
            ArrayList<NodeType> list = new ArrayList<NodeType>();
            list.add(nType);
            table.put(nType.name, list);
        }
    }

    /*
     * Function: Look up the type of declaration if exists in the HashMap table
     * loopup definition for name
     * Param: NodeType to be looked up
     * Return: Returns NodeType of if it exists, null otherwise
     */
    public NodeType lookup(String name) {
        if (table.containsKey(name)) {
            ArrayList<NodeType> list = table.get(name);
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).name.equals(name)) {
                    return list.get(i);
                }
            }
        }
        return null;
    }

    /*
     * Function: Delete NodeType from list in the HashMap table
     * Param: Level to be deleted
     * Return: void
     */
    public void delete(int level) {
        for (String key : table.keySet()) {
            ArrayList<NodeType> list = table.get(key);
            for (int i = 0; i < list.size(); i++) {
                NodeType nType = list.get(i);
                if (nType.level == level) {
                    String type = "";
                    if (isInteger(nType.def)) {
                        type = "int";
                    } else
                        type = "void";
                    indent(level + 1);
                    writeToFile(nType.name + ": " + type, true);
                    list.remove(i);
                }
            }
        }
    }

    /******************************** visitors ********************************/
    public void visit(NameTy exp, int level) {

    }
    /* Lists */

    public void visit(DecList decList, int level) {
        indent(level);
        writeToFile("Entering global scope", true);
        // Input function
        VarDecList inputParameters = new VarDecList(null, null);
        CompoundExp inputCompundtreeNode = new CompoundExp(0, 0, null, null);
        NodeType inputNode = new NodeType("input",
                new FunctionDec(0, 0, new NameTy(0, 0, 1), "input", inputParameters, inputCompundtreeNode), level);
        insert(inputNode);

        // Output function
        VarDecList outputParameters = new VarDecList(null, null);
        CompoundExp outputCompundtreeNode = new CompoundExp(0, 0, null, null);
        NodeType outputNode = new NodeType("output",
                new FunctionDec(0, 0, new NameTy(0, 0, 0), "output", outputParameters, outputCompundtreeNode), level);
        insert(outputNode);
        while (decList != null) {
            if (decList.head != null) {
                decList.head.accept(this, level + 1);
            }
            decList = decList.tail;
        }
        delete(level);
        indent(level);
        writeToFile("Leaving global scope", true);
    }

    public void visit(VarDecList varDecList, int level) {
        while (varDecList != null) {
            if (varDecList.head != null) {
                varDecList.head.accept(this, level);
            }
            varDecList = varDecList.tail;
        }
    }

    public void visit(ExpList expList, int level) {
        while (expList != null) {
            if (expList.head != null) {
                expList.head.accept(this, level);
            }
            expList = expList.tail;
        }
    }

    /* Vars */
    public void visit(SimpleVar simpleVar, int level) {
        if (lookup(simpleVar.name) == null) {
            System.err.println("Invalid use of undeclared variable '" + simpleVar.name +
                    "'' at: " + simpleVar.row
                    + " column: " + simpleVar.col);
        }
    }

    public void visit(IndexVar indexVar, int level) {
        indexVar.index.accept(this, level + 1);
        if (lookup(indexVar.name) == null) {
            System.err.println("Invalid use of undeclared variable '" + indexVar.name +
                    "'' at: " + indexVar.row
                    + " column: " + indexVar.col);
        }
    }

    /* Expressions */
    public void visit(NilExp exp, int level) {
        exp.dtype = new SimpleDec(exp.row, exp.col, new NameTy(exp.row, exp.col, 0), "NilExpression");
    }

    public void visit(VarExp exp, int level) {
        if (exp.variable != null) {
            exp.variable.accept(this, level + 1);
        }
        NodeType nodeType = lookup(exp.variable.name);
        if (nodeType != null)
            exp.dtype = nodeType.def;
    }

    public void visit(IntExp exp, int level) {
        exp.dtype = new SimpleDec(exp.row, exp.col, new NameTy(exp.row, exp.col, 1), "IntExpression");
    }

    public void visit(CallExp exp, int level) {
        if (exp.args != null)
            exp.args.accept(this, level + 1);
        NodeType expType = lookup(exp.func);
        if (expType != null) {
            int type = expType.def.typ.typ;
            exp.dtype = new SimpleDec(exp.row, exp.col, new NameTy(exp.row, exp.col, type), exp.func);
        } else {
            System.err.println("Invalid call to undefined function at line: " + exp.row + " column: " + exp.col);
        }
    }

    public void visit(OpExp exp, int level) {
        exp.left.accept(this, level + 1);
        exp.right.accept(this, level + 1);

        if (isInteger(exp.left.dtype) && isInteger(exp.right.dtype)) {
            exp.dtype = exp.left.dtype;
        }
        if (!isInteger(exp.left.dtype) && !isInteger(exp.right.dtype)) {
            exp.dtype = exp.left.dtype;
            System.err.println("Invalid operation between two void types at line: " + exp.row + " column: " + exp.col);
        } else if (isInteger(exp.left.dtype) && !isInteger(exp.right.dtype)) {
            exp.dtype = exp.left.dtype;
            System.err.println("Invalid operation between int and void at line: " + exp.row + " column: " + exp.col);
        } else if (!isInteger(exp.left.dtype) && isInteger(exp.right.dtype)) {
            exp.dtype = exp.right.dtype;
            System.err.println("Invalid operation between void and int at line: " + exp.row + " column: " + exp.col);
        }

    }

    public void visit(AssignExp exp, int level) {
        exp.lhs.accept(this, level + 1);
        exp.rhs.accept(this, level + 1);

        if (isInteger(exp.lhs.dtype) && isInteger(exp.rhs.dtype)) {
            exp.dtype = exp.lhs.dtype;
        }
        if (!isInteger(exp.lhs.dtype) && !isInteger(exp.rhs.dtype)) {
            exp.dtype = exp.lhs.dtype;
            System.err.println("Invalid assignment between void and void at line: " + exp.row + " column: " + exp.col);
        } else if (isInteger(exp.lhs.dtype) && !isInteger(exp.rhs.dtype)) {
            exp.dtype = exp.lhs.dtype;
            System.err.println("Invalid assignment between int and void at line: " + exp.row + " column: " + exp.col);
        } else if (!isInteger(exp.lhs.dtype) && isInteger(exp.rhs.dtype)) {
            exp.dtype = exp.lhs.dtype;
            System.err.println("Invalid assignment between void and int at line: " + exp.row + " column: " + exp.col);
        }
    }

    public void visit(IfExp exp, int level) {
        exp.test.accept(this, level);
        exp.thenpart.accept(this, level);
        if (exp.elsepart != null)
            exp.elsepart.accept(this, level);
        exp.dtype = new SimpleDec(exp.row, exp.col, new NameTy(exp.row, exp.col, 1), "IfExpression");
        if (isInteger(exp.test.dtype)) {
            System.err.println("Invalid test expression for if statement at line: " + exp.row + " column: " + exp.col);
        }
    }

    public void visit(WhileExp exp, int level) {
        if (exp.test != null)
            exp.test.accept(this, level);
        if (exp.body != null)
            exp.body.accept(this, level);
    }

    public void visit(ReturnExp exp, int level) {
        if (exp.exp != null)
            exp.exp.accept(this, level + 1);
        exp.dtype = exp.exp.dtype;
    }

    public void visit(CompoundExp exp, int level) {
        indent(level);
        writeToFile("Entering a new block: ", true);
        if (exp.decs != null)
            exp.decs.accept(this, level + 1);
        if (exp.exps != null)
            exp.exps.accept(this, level + 1);
        delete(level);
        indent(level);
        writeToFile("Leaving the block ", true);
    }

    /* Declarations */
    public void visit(FunctionDec funcDec, int level) {
        insert(new NodeType(funcDec.func, funcDec, level - 1));
        indent(level);
        writeToFile("Entering the scope of function: " + funcDec.func, true);
        if (funcDec.params != null)
            funcDec.params.accept(this, level + 1);

        if (funcDec.body != null)
            funcDec.body.accept(this, level);

        delete(level);
        indent(level);
        writeToFile("Leaving the scope of function", true);
    }

    public void visit(SimpleDec simpleDec, int level) {
        NodeType nodeType = new NodeType(simpleDec.name, simpleDec, level - 1);
        NodeType alreadyExists = lookup(simpleDec.name);

        if (alreadyExists != null && alreadyExists.level == level - 1)
            System.err.println("Invalid declaration of '" + simpleDec.name + "'. variable already declared");
        if (!isInteger(nodeType.def))
            System.err.println(
                    "Invalid declaration of void '" + simpleDec.name
                            + "'. void variable does not semantically make sense.");

        insert(nodeType);
    }

    public void visit(ArrayDec arrayDec, int level) {
        NodeType nodeType = new NodeType(arrayDec.name, arrayDec, level - 1);
        NodeType alreadyExists = lookup(arrayDec.name);

        if (alreadyExists != null && alreadyExists.level == level)
            System.err.println("Invalid declaration of '" + arrayDec.name + "'. variable already declared");

        if (!isInteger(nodeType.def))
            System.err.println(
                    "Invalid declaration of void " + arrayDec.name
                            + "[] void variable does not semantically make sense.");

        insert(nodeType);
    }

}