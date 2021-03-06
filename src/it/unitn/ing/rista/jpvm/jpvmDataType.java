/* jpvmDataType.java
 *
 * Integral constants representing data type names of interest to
 * the jpvm system - i.e. these are the types that can be packed
 * into jpvm buffers.
 *
 * Adam J Ferrari
 * Sun 05-26-1996
 *
 * modified by
 * Luca Lutterotti
 * Verona, 12 November 1999
 *
 * Copyright (C) 1996  Adam J Ferrari, 1999 Luca Lutterotti
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 675 Mass Ave, Cambridge,
 * MA 02139, USA.
 */

package it.unitn.ing.rista.jpvm;

/**
 *
 * @version $Revision: 1.3 $, $Date: 2004/01/21 11:14:51 $
 * @author Adam J Ferrari
 * @author Luca Lutterotti
 * @since JDK1.1
 */

public final class jpvmDataType {
  public static final int jpvmNull = 0;
  public static final int jpvmByte = 1;
  public static final int jpvmChar = 2;
  public static final int jpvmShort = 3;
  public static final int jpvmInteger = 4;
  public static final int jpvmLong = 5;
  public static final int jpvmFloat = 6;
  public static final int jpvmDouble = 7;
  public static final int jpvmString = 8;
  public static final int jpvmTid = 9;
}

;
