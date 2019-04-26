/*
 * Copyright 2019 Bart Verhoeven
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
 */
package be.nepherte.bingmaps;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class CapturingOutputStream extends ServletOutputStream {

  private final List<Byte> written;
  private boolean writeListenerCalled;

  public CapturingOutputStream() {
    written = new ArrayList<>(8092);
  }

  @Override
  public void write(int i) throws IOException {
    //noinspection NumericCastThatLosesPrecision see contract of method
    byte b = (byte) i;
    written.add(b);
  }

  public byte[] getValue() {
    int byteCount = written.size();
    byte[] value = new byte[byteCount];

    for (int i = 0; i < byteCount; i++) {
      //noinspection AutoUnboxing see write(int) implementation
      value[i] = written.get(i);
    }

    return value;
  }

  @Override
  public boolean isReady() {
    return true;
  }

  @Override
  public void setWriteListener(WriteListener writeListener) {
    if (writeListener == null) {
      throw new NullPointerException("writeListener is [" + null + "]");
    }
    if (writeListenerCalled) {
      throw new IllegalStateException("setWriteListener called " +
        "more than once within the scope of the same request"
      );
    }

    writeListenerCalled = true;

    try {
      writeListener.onWritePossible();
    }
    catch (IOException exception) {
      throw new RuntimeException(exception);
    }
  }
}
