/*
 * Copyright 2021 Miroslav Pokorny (github.com/mP1)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package walkingkooka.tree.json.patch;

import org.junit.jupiter.api.Test;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonPropertyName;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContexts;
import walkingkooka.tree.json.patch.PatchableTestingTest.TestPatchable;

import java.util.Objects;

public final class PatchableTestingTest implements PatchableTesting<TestPatchable> {

    private final static String BEFORE = "before";
    private final static String AFTER = "after";

    @Test
    public void testPatchAndCheck() {
        this.patchAndCheck(this.createPatchable(), JsonNode.string(BEFORE));
    }

    @Test
    public void testPatchAndCheck2() {
        this.patchAndCheck(
                this.createPatchable(),
                this.createPatch(),
                new TestPatchable(AFTER)
        );
    }

    @Test
    public void testPatchJsonStringAndCheck() {
        this.patchAndCheck(
                this.createPatchable(),
                this.createPatch().toString(),
                new TestPatchable(AFTER)
        );
    }

    @Test
    public void testPatchInvalidProperty() {
        final JsonPropertyName name = JsonPropertyName.with("abc");
        final JsonNode value = JsonNode.string("xyz");

        this.patchInvalidPropertyFails(
                JsonNode.object()
                        .set(name, value),
                name,
                value
        );
    }

    @Test
    public void testPatchInvalidPropertyStringOverloads() {
        final JsonPropertyName name = JsonPropertyName.with("abc");
        final JsonNode value = JsonNode.string("xyz");

        this.patchInvalidPropertyFails(
                JsonNode.object()
                        .set(name, value).toString(),
                name.value(),
                value
        );
    }

    @Override
    public TestPatchable createPatchable() {
        return new TestPatchable(BEFORE);
    }

    @Override
    public JsonNode createPatch() {
        return JsonNode.string(AFTER);
    }

    @Override
    public JsonNodeUnmarshallContext createPatchContext() {
        return CONTEXT;
    }

    final static JsonNodeUnmarshallContext CONTEXT = JsonNodeUnmarshallContexts.fake();

    final class TestPatchable implements Patchable<TestPatchable> {


        TestPatchable(final String value) {
            this.value = value;
        }

        @Override
        public TestPatchable patch(final JsonNode json,
                                   final JsonNodeUnmarshallContext context) {
            Objects.requireNonNull(json, "json");
            Objects.requireNonNull(context, "context");
            checkEquals(PatchableTestingTest.CONTEXT, context, "context");

            if (json.isObject()) {
                final JsonNode first = json.objectOrFail().firstChild().get();
                Patchable.invalidPropertyPresent(first.name(), first);
            }

            return new TestPatchable(json.stringOrFail());
        }

        private final String value;

        @Override
        public int hashCode() {
            return this.value.hashCode();
        }

        @Override
        public boolean equals(final Object other) {
            return other instanceof TestPatchable && ((TestPatchable) other).value.equals(this.value);
        }

        @Override
        public String toString() {
            return this.value;
        }
    }
}