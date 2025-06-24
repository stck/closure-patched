package com.sencha.tools.compiler.ast.js;

public class ObjectLiteral extends NodeContainer<ObjectProperty> {
  @Override
  <T> void doVisit(NodeVisitor<T> vis) {
    vis.onObjectLiteral(this);
  }

  private void addCustomProp(BaseNode name, BaseNode val) {
    ObjectProperty prop = new ObjectProperty();
    prop.setName(name);
    prop.setValue(val);
    prop.setOptimized(val);
    this.addElement(prop);
  }

  public void addProperty(BaseNode el) {
    if (el instanceof FunctionNode) {
      FunctionNode func = (FunctionNode)el;
      this.addCustomProp(func.getName(), func);
    } else if (el instanceof GetAccessor) {
      GetAccessor acc = (GetAccessor)el;
      this.addCustomProp(acc.getName(), acc);
    } else if (el instanceof SetAccessor) {
      SetAccessor acc = (SetAccessor)el;
      this.addCustomProp(acc.getName(), acc);
    } else if (el instanceof SpreadExpression) {
      SpreadExpression spe = (SpreadExpression)el;
      this.addCustomProp(spe.getOperand(), spe);
    } else if (el instanceof ObjectSpread) {
      ObjectSpread objspe = (ObjectSpread)el;
      this.addCustomProp(objspe.getOperand(), objspe);
    } else if (el instanceof OptionalMemberExpression) {
      OptionalMemberExpression optionalMemberExpression =
          (OptionalMemberExpression)el;
      this.addCustomProp(optionalMemberExpression.getLeft(),
                         optionalMemberExpression.getRight());
    } else if (el instanceof RestParameter) {
      RestParameter rest = (RestParameter)el;
      this.addCustomProp(rest.getOperand(), rest);
    } else if (el instanceof DefaultParameter) {
      DefaultParameter defaultParam = (DefaultParameter)el;
      this.addCustomProp(defaultParam.getName(),
                         defaultParam.getDefaultValue());
    } else {
      this.addElement((ObjectProperty)el);
    }
  }
}
