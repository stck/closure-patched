package com.sencha.tools.compiler.ast;

import com.google.javascript.jscomp.parsing.parser.trees.ArgumentListTree;
import com.google.javascript.jscomp.parsing.parser.trees.ArrayLiteralExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.ArrayPatternTree;
import com.google.javascript.jscomp.parsing.parser.trees.AwaitExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.BinaryOperatorTree;
import com.google.javascript.jscomp.parsing.parser.trees.BlockTree;
import com.google.javascript.jscomp.parsing.parser.trees.BreakStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.CallExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.CaseClauseTree;
import com.google.javascript.jscomp.parsing.parser.trees.CatchTree;
import com.google.javascript.jscomp.parsing.parser.trees.ClassDeclarationTree;
import com.google.javascript.jscomp.parsing.parser.trees.CommaExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.Comment;
import com.google.javascript.jscomp.parsing.parser.trees.ComprehensionForTree;
import com.google.javascript.jscomp.parsing.parser.trees.ComprehensionIfTree;
import com.google.javascript.jscomp.parsing.parser.trees.ComprehensionTree;
import com.google.javascript.jscomp.parsing.parser.trees.ComputedPropertyDefinitionTree;
import com.google.javascript.jscomp.parsing.parser.trees.ComputedPropertyGetterTree;
import com.google.javascript.jscomp.parsing.parser.trees.ComputedPropertyMethodTree;
import com.google.javascript.jscomp.parsing.parser.trees.ComputedPropertySetterTree;
import com.google.javascript.jscomp.parsing.parser.trees.ConditionalExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.ContinueStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.DebuggerStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.DefaultClauseTree;
import com.google.javascript.jscomp.parsing.parser.trees.DefaultParameterTree;
import com.google.javascript.jscomp.parsing.parser.trees.DoWhileStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.DynamicImportTree;
import com.google.javascript.jscomp.parsing.parser.trees.EmptyStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.ExportDeclarationTree;
import com.google.javascript.jscomp.parsing.parser.trees.ExportSpecifierTree;
import com.google.javascript.jscomp.parsing.parser.trees.ExpressionStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.FieldDeclarationTree;
import com.google.javascript.jscomp.parsing.parser.trees.FinallyTree;
import com.google.javascript.jscomp.parsing.parser.trees.ForAwaitOfStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.ForInStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.ForOfStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.ForStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.FormalParameterListTree;
import com.google.javascript.jscomp.parsing.parser.trees.FunctionDeclarationTree;
import com.google.javascript.jscomp.parsing.parser.trees.GetAccessorTree;
import com.google.javascript.jscomp.parsing.parser.trees.IdentifierExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.IfStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.ImportDeclarationTree;
import com.google.javascript.jscomp.parsing.parser.trees.ImportMetaExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.ImportSpecifierTree;
import com.google.javascript.jscomp.parsing.parser.trees.IterRestTree;
import com.google.javascript.jscomp.parsing.parser.trees.IterSpreadTree;
import com.google.javascript.jscomp.parsing.parser.trees.LabelledStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.LiteralExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.MemberExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.MemberLookupExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.MissingPrimaryExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.NewExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.NewTargetExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.NullTree;
import com.google.javascript.jscomp.parsing.parser.trees.ObjectLiteralExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.ObjectPatternTree;
import com.google.javascript.jscomp.parsing.parser.trees.ObjectRestTree;
import com.google.javascript.jscomp.parsing.parser.trees.ObjectSpreadTree;
import com.google.javascript.jscomp.parsing.parser.trees.OptChainCallExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.OptionalMemberExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.OptionalMemberLookupExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.ParenExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;
import com.google.javascript.jscomp.parsing.parser.trees.ProgramTree;
import com.google.javascript.jscomp.parsing.parser.trees.PropertyNameAssignmentTree;
import com.google.javascript.jscomp.parsing.parser.trees.ReturnStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.SetAccessorTree;
import com.google.javascript.jscomp.parsing.parser.trees.SuperExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.SwitchStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.TemplateLiteralExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.TemplateLiteralPortionTree;
import com.google.javascript.jscomp.parsing.parser.trees.TemplateSubstitutionTree;
import com.google.javascript.jscomp.parsing.parser.trees.ThisExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.ThrowStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.TryStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.UnaryExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.UpdateExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.VariableDeclarationListTree;
import com.google.javascript.jscomp.parsing.parser.trees.VariableDeclarationTree;
import com.google.javascript.jscomp.parsing.parser.trees.VariableStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.WhileStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.WithStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.YieldExpressionTree;
import com.sencha.tools.compiler.ast.js.BaseNode;

public interface ClosureASTConverter {
   BaseNode convert(ArgumentListTree var1);

   BaseNode convert(ArrayLiteralExpressionTree var1);

   BaseNode convert(ArrayPatternTree var1);

   BaseNode convert(AwaitExpressionTree var1);

   BaseNode convert(BinaryOperatorTree var1);

   BaseNode convert(BlockTree var1);

   BaseNode convert(BreakStatementTree var1);

   BaseNode convert(CallExpressionTree var1);

   BaseNode convert(CaseClauseTree var1);

   BaseNode convert(CatchTree var1);

   BaseNode convert(ClassDeclarationTree var1);

   BaseNode convert(CommaExpressionTree var1);

   BaseNode convert(Comment var1);

   BaseNode convert(ComprehensionForTree var1);

   BaseNode convert(ComprehensionIfTree var1);

   BaseNode convert(ComprehensionTree var1);

   BaseNode convert(ComputedPropertyDefinitionTree var1);

   BaseNode convert(ComputedPropertyGetterTree var1);

   BaseNode convert(ComputedPropertyMethodTree var1);

   BaseNode convert(ComputedPropertySetterTree var1);

   BaseNode convert(ConditionalExpressionTree var1);

   BaseNode convert(ContinueStatementTree var1);

   BaseNode convert(DebuggerStatementTree var1);

   BaseNode convert(DefaultClauseTree var1);

   BaseNode convert(DefaultParameterTree var1);

   BaseNode convert(DoWhileStatementTree var1);

   BaseNode convert(DynamicImportTree var1);

   BaseNode convert(EmptyStatementTree var1);

   BaseNode convert(ExportDeclarationTree var1);

   BaseNode convert(ExportSpecifierTree var1);

   BaseNode convert(ExpressionStatementTree var1);

   BaseNode convert(FieldDeclarationTree var1);

   BaseNode convert(FinallyTree var1);

   BaseNode convert(ForAwaitOfStatementTree var1);

   BaseNode convert(ForInStatementTree var1);

   BaseNode convert(FormalParameterListTree var1);

   BaseNode convert(ForOfStatementTree var1);

   BaseNode convert(ForStatementTree var1);

   BaseNode convert(FunctionDeclarationTree var1);

   BaseNode convert(GetAccessorTree var1);

   BaseNode convert(IdentifierExpressionTree var1);

   BaseNode convert(IfStatementTree var1);

   BaseNode convert(ImportDeclarationTree var1);

   BaseNode convert(ImportMetaExpressionTree var1);

   BaseNode convert(ImportSpecifierTree var1);

   BaseNode convert(IterRestTree var1);

   BaseNode convert(IterSpreadTree var1);

   BaseNode convert(LabelledStatementTree var1);

   BaseNode convert(LiteralExpressionTree var1);

   BaseNode convert(MemberExpressionTree var1);

   BaseNode convert(MemberLookupExpressionTree var1);

   BaseNode convert(MissingPrimaryExpressionTree var1);

   BaseNode convert(NewExpressionTree var1);

   BaseNode convert(NewTargetExpressionTree var1);

   BaseNode convert(NullTree var1);

   BaseNode convert(ObjectLiteralExpressionTree var1);

   BaseNode convert(ObjectPatternTree var1);

   BaseNode convert(ObjectRestTree var1);

   BaseNode convert(ObjectSpreadTree var1);

   BaseNode convert(OptChainCallExpressionTree var1);

   BaseNode convert(OptionalMemberExpressionTree var1);

   BaseNode convert(OptionalMemberLookupExpressionTree var1);

   BaseNode convert(ParenExpressionTree var1);

   BaseNode convert(ParseTree var1);

   BaseNode convert(ProgramTree var1);

   BaseNode convert(PropertyNameAssignmentTree var1);

   BaseNode convert(ReturnStatementTree var1);

   BaseNode convert(SetAccessorTree var1);

   BaseNode convert(SuperExpressionTree var1);

   BaseNode convert(SwitchStatementTree var1);

   BaseNode convert(TemplateLiteralExpressionTree var1);

   BaseNode convert(TemplateLiteralPortionTree var1);

   BaseNode convert(TemplateSubstitutionTree var1);

   BaseNode convert(ThisExpressionTree var1);

   BaseNode convert(ThrowStatementTree var1);

   BaseNode convert(TryStatementTree var1);

   BaseNode convert(UnaryExpressionTree var1);

   BaseNode convert(UpdateExpressionTree var1);

   BaseNode convert(VariableDeclarationListTree var1);

   BaseNode convert(VariableDeclarationTree var1);

   BaseNode convert(VariableStatementTree var1);

   BaseNode convert(WhileStatementTree var1);

   BaseNode convert(WithStatementTree var1);

   BaseNode convert(YieldExpressionTree var1);

   BaseNode doConvert(ParseTree var1, BaseNode var2);
}
