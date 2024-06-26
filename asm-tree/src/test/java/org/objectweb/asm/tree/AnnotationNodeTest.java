// ASM: a very small and fast Java bytecode manipulation framework
// Copyright (c) 2000-2011 INRIA, France Telecom
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions
// are met:
// 1. Redistributions of source code must retain the above copyright
//    notice, this list of conditions and the following disclaimer.
// 2. Redistributions in binary form must reproduce the above copyright
//    notice, this list of conditions and the following disclaimer in the
//    documentation and/or other materials provided with the distribution.
// 3. Neither the name of the copyright holders nor the names of its
//    contributors may be used to endorse or promote products derived from
//    this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
// THE POSSIBILITY OF SUCH DAMAGE.
package org.objectweb.asm.tree;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.test.AsmTest;

/**
 * Unit tests for {@link AnnotationNode}.
 *
 * @author Eric Bruneton
 */
class AnnotationNodeTest extends AsmTest {

  @Test
  void testConstructor() {
    AnnotationNode annotationNode = new AnnotationNode("LI;");

    assertEquals("LI;", annotationNode.desc);
  }

  @Test
  void testConstructor_illegalState() {
    Executable constructor = () -> new AnnotationNode("LI;") {};

    assertThrows(IllegalStateException.class, constructor);
  }

  @Test
  void testVisit() {
    AnnotationNode annotationNode = new AnnotationNode("LI;");

    annotationNode.visit("bytes", new byte[] {0, 1});
    annotationNode.visit("booleans", new boolean[] {false, true});
    annotationNode.visit("shorts", new short[] {0, 1});
    annotationNode.visit("chars", new char[] {'0', '1'});
    annotationNode.visit("ints", new int[] {0, 1});
    annotationNode.visit("longs", new long[] {0L, 1L});
    annotationNode.visit("floats", new float[] {0.0f, 1.0f});
    annotationNode.visit("doubles", new double[] {0.0, 1.0});
    annotationNode.visit("string", "value");
    annotationNode.visitAnnotation("annotation", "Lpkg/Annotation;");

    assertEquals("bytes", annotationNode.values.get(0));
    assertEquals(List.of((byte) 0, (byte) 1), annotationNode.values.get(1));
    assertEquals("booleans", annotationNode.values.get(2));
    assertEquals(List.of(false, true), annotationNode.values.get(3));
    assertEquals("shorts", annotationNode.values.get(4));
    assertEquals(List.of((short) 0, (short) 1), annotationNode.values.get(5));
    assertEquals("chars", annotationNode.values.get(6));
    assertEquals(List.of('0', '1'), annotationNode.values.get(7));
    assertEquals("ints", annotationNode.values.get(8));
    assertEquals(List.of(0, 1), annotationNode.values.get(9));
    assertEquals("longs", annotationNode.values.get(10));
    assertEquals(List.of(0L, 1L), annotationNode.values.get(11));
    assertEquals("floats", annotationNode.values.get(12));
    assertEquals(List.of(0.0f, 1.0f), annotationNode.values.get(13));
    assertEquals("doubles", annotationNode.values.get(14));
    assertEquals(List.of(0.0, 1.0), annotationNode.values.get(15));
    assertEquals("string", annotationNode.values.get(16));
    assertEquals("value", annotationNode.values.get(17));
    assertEquals("annotation", annotationNode.values.get(18));
    assertEquals("Lpkg/Annotation;", ((AnnotationNode) annotationNode.values.get(19)).desc);
  }

  @Test
  void testAnnotationNode_accept_skipNestedAnnotations() {
    AnnotationNode annotationNode = new AnnotationNode("LI;");
    annotationNode.visit("bytes", new byte[] {0, 1});
    annotationNode.visitAnnotation("annotation", "Lpkg/Annotation;");
    AnnotationNode dstAnnotationNode = new AnnotationNode("LJ;");
    AnnotationVisitor skipNestedAnnotationsVisitor =
        new AnnotationVisitor(/* latest */ Opcodes.ASM10_EXPERIMENTAL, dstAnnotationNode) {

          @Override
          public AnnotationVisitor visitAnnotation(final String name, final String descriptor) {
            return null;
          }

          @Override
          public AnnotationVisitor visitArray(final String name) {
            return null;
          }
        };

    annotationNode.accept(skipNestedAnnotationsVisitor);

    assertNull(dstAnnotationNode.values);
  }
}
