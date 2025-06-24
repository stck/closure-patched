package com.sencha.tools.compiler.ast;

import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;
import com.sencha.tools.compiler.ast.js.BaseNode;

public interface ClosureNodeConverter {
   BaseNode convert(ParseTree var1, ClosureASTConverter var2);
}
