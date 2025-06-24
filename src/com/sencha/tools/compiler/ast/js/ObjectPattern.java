package com.sencha.tools.compiler.ast.js;

/**
 * Represents an object destructuring pattern in JavaScript, like {a, b, c = 1} in function parameters.
 * This differs from ObjectLiteral in that it preserves DefaultParameter nodes instead of converting
 * them to name-value pairs.
 */
public class ObjectPattern extends ObjectLiteral {
    
    @Override
    public void addProperty(BaseNode el) {
        if (el instanceof DefaultParameter) {
            // For destructuring patterns, preserve the DefaultParameter as-is
            // We need to wrap it in an ObjectProperty to fit the container structure
            DefaultParameter defaultParam = (DefaultParameter)el;
            ObjectProperty prop = new ObjectProperty();
            prop.setName(defaultParam.getName());
            prop.setValue(defaultParam); // Keep the entire DefaultParameter as the value
            prop.setOptimized(defaultParam);
            this.addElement(prop);
        } else {
            // Use the parent class logic for all other cases
            super.addProperty(el);
        }
    }
    
    @Override
    <T> void doVisit(NodeVisitor<T> vis) {
        vis.onObjectPattern(this);
    }
} 