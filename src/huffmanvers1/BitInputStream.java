package huffmanvers1;

/**
 *
 * @author user
 */
// $Id$

import java.io.*;

public class BitInputStream
{
  private InputStream in;
  private int buffer;
  private int nextBit = 8;

  public BitInputStream( InputStream in ) {
    this.in = in;
  }

  synchronized public int readBit() throws IOException {
    if (in == null)
      throw new IOException( "Already closed" );

    if (nextBit==8) {
      buffer = in.read();

      if (buffer==-1)
        throw new EOFException();

      nextBit = 0;
    }

    int bit = buffer & (1<<nextBit);
    nextBit++;

    bit = bit == 0 ? 0 : 1;

    return bit;
  }

  public void close() throws IOException {
    in.close();
    in = null;
  }
}
