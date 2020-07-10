package software.amazon.smithy.codegen.core;

import software.amazon.smithy.model.shapes.Shape;

class SymbolProviderTestHelper implements SymbolProvider {

    @Override
    public Symbol toSymbol(Shape shape) {
        Symbol symbol = Symbol.builder().putProperty("shape", shape)
                .name("boolean")
                .namespace("namespace", "/")
                .definitionFile("namespace.ts").build();
        return symbol;
    }
}
