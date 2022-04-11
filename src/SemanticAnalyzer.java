import absyn.*;
import absyn.Decs.*;
import absyn.Exps.*;
import absyn.Lists.*;
import absyn.VarDecs.*;
import absyn.Vars.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class SemanticAnalyzer implements AbsynVisitor {

    final static int SPACES = 4;
    public String codeName;
    public HashMap<String, ArrayList<NodeType>> symTable;

    public SemanticAnalyzer(String codeName) {
        symTable = new HashMap<String, ArrayList<NodeType>>();
        this.codeName = codeName;
    }

    private void writeToFile(String string) {
        try {
            FileWriter myWriter = new FileWriter("Output" + "/" + codeName + ".sym", true);
            myWriter.write(string);
            myWriter.close();
        } catch (IOException e) {
            System.out.println("FileWriter unsuccessful");
            e.printStackTrace();
        }
    }

    private void indent(int level) {
        for (int i = 0; i < level * SPACES; i++)
            writeToFile(" ");
    }

    public boolean isInteger(Dec dtype) 
    {
        if(dtype == null)
        {
            System.err.println("Invalid type found");
        }
        else if (dtype.typ.typ == 1) 
        {
            return true;
        }
        return false;
    }

    /*
     * Function: Inserts NodeType in the symTable HashMap ArrayList
     * Param: NodeType to be intsered
     * returns: void
     */
    public void insert(NodeType nType) {
        if (symTable.containsKey(nType.name)) {
            ArrayList<NodeType> list = symTable.get(nType.name);
            list.add(nType);
        } else {
            ArrayList<NodeType> list = new ArrayList<NodeType>();
            list.add(nType);
            symTable.put(nType.name, list);
        }
    }

    /*
     * Function: Look up the type of declaration if exists in the HashMap symTable
     * loopup definition for name
     * Param: NodeType to be looked up
     * Return: Returns NodeType of if it exists, null otherwise
     */
    public NodeType lookup(String name) {
        if (symTable.containsKey(name)) {
            ArrayList<NodeType> list = symTable.get(name);
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).name.equals(name)) {
                    return list.get(i);
                }
            }
        }
        return null;
    }

    /*
     * Function: Delete NodeType from list in the HashMap symTable
     * Param: Level to be deleted
     * Return: void
     */
    public void delete(int level) {
        for (String key : symTable.keySet()) {
            ArrayList<NodeType> list = symTable.get(key);
            for (int i = 0; i < list.size(); i++) {
                NodeType nType = list.get(i);
                if (nType.level == level) {
                    String type = "";
                    if (isInteger(nType.def)) {
                        type = "int";
                    } else
                        type = "void";
                    indent(level + 1);
                    writeToFile(nType.name + ": " + type + "\n");
                    list.remove(i);
                }
            }
        }
    }

    /******************************** visitors ********************************/
    public void visit(NameTy exp, int level, boolean isAddress) {

    }
    /* Lists */

    public void visit(DecList decList, int level, boolean isAddress) {
        indent(level);
        writeToFile("Entering global scope\n");
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
                decList.head.accept(this, level + 1, false);
            }
            decList = decList.tail;
        }
        delete(level);
        indent(level);
        writeToFile("Leaving global scope\n");
    }

    public void visit(VarDecList varDecList, int level, boolean isAddress) {
        while (varDecList != null) {
            if (varDecList.head != null) {
                varDecList.head.accept(this, level, false);
            }
            varDecList = varDecList.tail;
        }
    }

    public void visit(ExpList expList, int level, boolean isAddress) {
        while (expList != null) {
            if (expList.head != null) {
                expList.head.accept(this, level, false);
            }
            expList = expList.tail;
        }
    }

    /* Vars */
    public void visit(SimpleVar simpleVar, int level, boolean isAddress) {
        if (lookup(simpleVar.name) == null) {
            System.err.println("Invalid use of undeclared variable '" + simpleVar.name +
                    "'' at: " + simpleVar.row
                    + " column: " + simpleVar.col);
        }
    }

    public void visit(IndexVar indexVar, int level, boolean isAddress) {
        indexVar.index.accept(this, level + 1, false);
        if (lookup(indexVar.name) == null) {
            System.err.println("Invalid use of undeclared variable '" + indexVar.name +
                    "'' at: " + indexVar.row
                    + " column: " + indexVar.col);
        }
    }

    /* Expressions */
    public void visit(NilExp exp, int level, boolean isAddress) {
        exp.dtype = new SimpleDec(exp.row, exp.col, new NameTy(exp.row, exp.col, 0), "NilExpression");
    }

    public void visit(VarExp exp, int level, boolean isAddress) {
        if (exp.variable != null) {
            exp.variable.accept(this, level + 1, false);
        }
        NodeType nodeType = lookup(exp.variable.name);
        if (nodeType != null)
            exp.dtype = nodeType.def;
    }

    public void visit(IntExp exp, int level, boolean isAddress) {
        exp.dtype = new SimpleDec(exp.row, exp.col, new NameTy(exp.row, exp.col, 1), "IntExpression");
    }

    public void visit(CallExp exp, int level, boolean isAddress) {
        if (exp.args != null)
            exp.args.accept(this, level + 1, false);
        NodeType expType = lookup(exp.func);
        if (expType != null) {
            int type = expType.def.typ.typ;
            exp.dtype = new SimpleDec(exp.row, exp.col, new NameTy(exp.row, exp.col, type), exp.func);
        } else {
            System.err.println("Invalid call to undefined function at line: " + exp.row + " column: " + exp.col);
        }
    }

    public void visit(OpExp exp, int level, boolean isAddress) {
        exp.left.accept(this, level + 1, false);
        exp.right.accept(this, level + 1, false);

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

    public void visit(AssignExp exp, int level, boolean isAddress) {
        exp.lhs.accept(this, level + 1, false);
        exp.rhs.accept(this, level + 1, false);

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

    public void visit(IfExp exp, int level, boolean isAddress) {
        exp.test.accept(this, level, false);
        exp.thenpart.accept(this, level, false);
        if (exp.elsepart != null)
            exp.elsepart.accept(this, level, false);
        exp.dtype = new SimpleDec(exp.row, exp.col, new NameTy(exp.row, exp.col, 1), "IfExpression");
        if (!isInteger(exp.test.dtype)) {
            System.err.println("Invalid test expression for if statement at line: " + exp.row + " column: " + exp.col);
        }
    }

    public void visit(WhileExp exp, int level, boolean isAddress) {
        if (exp.test != null)
            exp.test.accept(this, level, false);
        if (exp.body != null)
            exp.body.accept(this, level, false);
    }

    public void visit(ReturnExp exp, int level, boolean isAddress) {
        if (exp.exp != null)
            exp.exp.accept(this, level + 1, false);
        exp.dtype = exp.exp.dtype;
    }

    public void visit(CompoundExp exp, int level, boolean isAddress) {
        indent(level);
        writeToFile("Entering a new block: \n");
        if (exp.decs != null)
            exp.decs.accept(this, level + 1, false);
        if (exp.exps != null)
            exp.exps.accept(this, level + 1, false);
        delete(level);
        indent(level);
        writeToFile("Leaving the block \n");
    }

    /* Declarations */
    public void visit(FunctionDec funcDec, int level, boolean isAddress) {
        insert(new NodeType(funcDec.func, funcDec, level - 1));
        indent(level);
        writeToFile("Entering the scope of function: " + funcDec.func + "\n");
        if (funcDec.params != null)
            funcDec.params.accept(this, level + 1, false);

        if (funcDec.body != null)
            funcDec.body.accept(this, level, false);

        delete(level);
        indent(level);
        writeToFile("Leaving the scope of function\n");
    }

    public void visit(SimpleDec simpleDec, int level, boolean isAddress) {
        NodeType nodeType = new NodeType(simpleDec.name, simpleDec, level - 1);
        NodeType alreadyExists = lookup(simpleDec.name);

        if (alreadyExists != null && alreadyExists.level == level - 1)
            System.err.println("Invalid redeclaration of variable '" + simpleDec.name + "' line: " + simpleDec.row
                    + " column: " + simpleDec.col);
        if (!isInteger(nodeType.def))
            System.err.println(
                    "Invalid declaration of void variable '" + simpleDec.name
                            + "' at line: " + simpleDec.row + " column: " + simpleDec.col);

        simpleDec.nestLevel = level;
        insert(nodeType);
    }

    public void visit(ArrayDec arrayDec, int level, boolean isAddress) {
        NodeType nodeType = new NodeType(arrayDec.name, arrayDec, level - 1);
        NodeType alreadyExists = lookup(arrayDec.name);

        if (alreadyExists != null && alreadyExists.level == level)
            System.err.println("Invalid declaration of '" + arrayDec.name + "'. variable already declared");

        if (!isInteger(nodeType.def))
            System.err.println(
                    "Invalid declaration of void " + arrayDec.name
                            + "[] void variable does not semantically make sense.");

        arrayDec.nestLevel = level;
        insert(nodeType);
    }

}