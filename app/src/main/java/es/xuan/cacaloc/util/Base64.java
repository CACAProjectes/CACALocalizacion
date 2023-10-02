package es.xuan.cacaloc.util;
/*
 * Project: RCD Online
 * Class: Base64
 * Version history:
 *  > 1.0 � DMR Consulting (25/03/2005)
 * 
 * This file is protected by international laws.
 */
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Manages Base64 strings conversions.  
 * ----------------
 * @author   DMR Consulting
 * @version  1.0//
 */
public final class Base64  {
    
/* ********  P U B L I C   F I E L D S  ******** */   
/**
 * comment for Logger
 */
//private static final Logger logger = Logger.getLogger(Base64.class);

    



    /** No options specified. Value is zero. */
    public static final int NO_OPTIONS = 0;
    
    /** Specify encoding. */
    public static final int ENCODE = 1;
    
    
    /** Specify decoding. */
    public static final int DECODE = 0;
    
    
    /** Specify that data should be gzip-compressed. */
    public static final int GZIP = 2;
    
    
    /** Don't break lines when encoding (violates strict Base64 specification) */
    public static final int DONT_BREAK_LINES = 8;
    
    
/* ********  P R I V A T E   F I E L D S  ******** */  
    
    
    /** Maximum line length (76) of Base64 output. */
    private static final int MAX_LINE_LENGTH = 76;
    
    
    /** The equals sign (=) as a byte. */
    private static final byte EQUALS_SIGN = (byte) '=';
    
    
    /** The new line character (\n) as a byte. */
    private static final byte NEW_LINE = (byte) '\n';
    
    
    /** Preferred encoding. */
    private static final String PREFERRED_ENCODING = "UTF-8";
    
    
    /** The 64 valid Base64 values. */
    private static final byte[] ALPHABET;
	/** NATIVE_ALPHABET. */
    private static final byte[] NATIVE_ALPHABET = /* May be something funny like EBCDIC */
    {
        (byte) 'A', (byte) 'B', (byte) 'C', (byte) 'D', (byte) 'E', (byte) 'F', (byte) 'G',
        (byte) 'H', (byte) 'I', (byte) 'J', (byte) 'K', (byte) 'L', (byte) 'M', (byte) 'N',
        (byte) 'O', (byte) 'P', (byte) 'Q', (byte) 'R', (byte) 'S', (byte) 'T', (byte) 'U', 
        (byte) 'V', (byte) 'W', (byte) 'X', (byte) 'Y', (byte) 'Z',
        (byte) 'a', (byte) 'b', (byte) 'c', (byte) 'd', (byte) 'e', (byte) 'f', (byte) 'g',
        (byte) 'h', (byte) 'i', (byte) 'j', (byte) 'k', (byte) 'l', (byte) 'm', (byte) 'n',
        (byte) 'o', (byte) 'p', (byte) 'q', (byte) 'r', (byte) 's', (byte) 't', (byte) 'u', 
        (byte) 'v', (byte) 'w', (byte) 'x', (byte) 'y', (byte) 'z',
        (byte) '0', (byte) '1', (byte) '2', (byte) '3', (byte) '4', (byte) '5', 
        (byte) '6', (byte) '7', (byte) '8', (byte) '9', (byte) '+', (byte) '/'
    };
    
    /** Determine which ALPHABET to use. */
    static {
        byte[] alphBytes;
        try {
            alphBytes = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".getBytes(PREFERRED_ENCODING);
        } catch (java.io.UnsupportedEncodingException use) {
            alphBytes = NATIVE_ALPHABET; // Fall back to native encoding
        }   // end catch
        ALPHABET = alphBytes;
    }   // end static
    
    
    /** 
     * Translates a Base64 value to either its 6-bit reconstruction value
     * or a negative number indicating some other meaning.
     **/
    private static final byte[] DECODABET =
    {   
        -9, -9, -9, -9, -9, -9, -9, -9, -9,             		// Decimal  0 -  8
        -5, -5,                                     			// Whitespace: Tab and Linefeed
        -9, -9,                                     			// Decimal 11 - 12
        -5,                                         			// Whitespace: Carriage Return
        -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9,     // Decimal 14 - 26
        -9, -9, -9, -9, -9,                             		// Decimal 27 - 31
        -5,                                         			// Whitespace: Space
        -9, -9, -9, -9, -9, -9, -9, -9, -9, -9,              	// Decimal 33 - 42
        62,                                        				// Plus sign at decimal 43
        -9, -9, -9,                                   			// Decimal 44 - 46
        63,                                         			// Slash at decimal 47
        52, 53, 54, 55, 56, 57, 58, 59, 60, 61,              	// Numbers zero through nine
        -9, -9, -9,                                   			// Decimal 58 - 60
        -1,                                         			// Equals sign at decimal 61
        -9, -9, -9,                                      		// Decimal 62 - 64
        0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13,           // Letters 'A' through 'N'
        14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25,        	// Letters 'O' through 'Z'
        -9, -9, -9, -9, -9, -9,                          		// Decimal 91 - 96
        26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38,		// Letters 'a' through 'm'
        39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51,		// Letters 'n' through 'z'
        -9, -9, -9, -9                                 			// Decimal 123 - 126
        /*, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, // Decimal 127 - 139
        -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9,     // Decimal 140 - 152
        -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9,     // Decimal 153 - 165
        -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9,     // Decimal 166 - 178
        -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9,     // Decimal 179 - 191
        -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9,     // Decimal 192 - 204
        -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9,     // Decimal 205 - 217
        -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9,     // Decimal 218 - 230
        -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9,     // Decimal 231 - 243
        -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9         	// Decimal 244 - 255 */
    };
    
    // I think I end up not using the BAD_ENCODING indicator.
    //private final static byte BAD_ENCODING    = -9; // Indicates error in encoding
	/** Defeats instantiation. */
    private static final byte WHITE_SPACE_ENC = -5; // Indicates white space in encoding
	/** Defeats instantiation. */
    private static final byte EQUALS_SIGN_ENC = -1; // Indicates equals sign in encoding
    
	/** Defeats instantiation. */
    private static final int BUFFER_SIZE = 2048;
    
	/** Defeats instantiation. */
    private static final int D3  = 3;
	/** Defeats instantiation. */
    private static final int D4  = 4;
	/** Defeats instantiation. */
    private static final int D6  = 6;
	/** Defeats instantiation. */
    private static final int D8  = 8;
	/** Defeats instantiation. */
    private static final int D12 = 12;
	/** Defeats instantiation. */
    private static final int D16 = 16;
	/** Defeats instantiation. */
    private static final int D18 = 18;
	/** Defeats instantiation. */
    private static final int D24 = 24;
    
	/** Defeats instantiation. */
    private static final int F3 = 0x3f;
    
	/** Defeats instantiation. */
    private static final int H_00FF = 0xff;
	/** Defeats instantiation. */
    private static final int H_007F = 0x7f;
	/** Defeats instantiation. */
    private static final int H_FF00 = 0xff00;
    
    /** Defeats instantiation. */
    private Base64() { }
    
    
    
/* ********  E N C O D I N G   M E T H O D S  ******** */    
    
    
    /**
     * Encodes up to the first three bytes of array <var>threeBytes</var>
     * and returns a four-byte array in Base64 notation.
     * The actual number of significant bytes in your array is
     * given by <var>numSigBytes</var>.
     * The array <var>threeBytes</var> needs only be as big as
     * <var>numSigBytes</var>.
     * Code can reuse a byte array by passing a four-byte array as <var>b4</var>.
     *
     * @param b4 A reusable byte array to reduce array instantiation
     * @param threeBytes the array to convert
     * @param numSigBytes the number of significant bytes in your array
     * @return four byte array in Base64 notation.
     * @since 1.5.1
     */
    private static byte[] encode3to4(final byte[] b4, final byte[] threeBytes, final int numSigBytes) {
        encode3to4(threeBytes, 0, numSigBytes, b4, 0);
        return b4;
    }   // end encode3to4

    
    /**
     * Encodes up to three bytes of the array <var>source</var>
     * and writes the resulting four Base64 bytes to <var>destination</var>.
     * The source and destination arrays can be manipulated
     * anywhere along their length by specifying 
     * <var>srcOffset</var> and <var>destOffset</var>.
     * This method does not check to make sure your arrays
     * are large enough to accomodate <var>srcOffset</var> + 3 for
     * the <var>source</var> array or <var>destOffset</var> + 4 for
     * the <var>destination</var> array.
     * The actual number of significant bytes in your array is
     * given by <var>numSigBytes</var>.
     *
     * @param source the array to convert
     * @param srcOffset the index where conversion begins
     * @param numSigBytes the number of significant bytes in your array
     * @param destination the array to hold the conversion
     * @param destOffset the index where output will be put
     * @return the <var>destination</var> array
     * @since 1.3
     */
    private static byte[] encode3to4(
     final byte[] source, final int srcOffset, final int numSigBytes,
     final byte[] destination, final int destOffset) {
        //           1         2         3  
        // 01234567890123456789012345678901 Bit position
        // --------000000001111111122222222 Array position from threeBytes
        // --------|    ||    ||    ||    | Six bit groups to index ALPHABET
        //          >>18  >>12  >> 6  >> 0  Right shift necessary
        //                0x3f  0x3f  0x3f  Additional AND
        
        // Create buffer with zero-padding if there are only one or two
        // significant bytes passed in the array.
        // We have to shift left 24 in order to flush out the 1's that appear
        // when Java treats a value as negative that is cast from a byte to an int.
        int inBuff = 0;
        if (numSigBytes > 0) {
            inBuff = (source[srcOffset] << D24) >>> D8;
        }
        if (numSigBytes > 1) {
            inBuff |= (source[srcOffset + 1] << D24) >>> D16;
        }
        if (numSigBytes > 2) {
            inBuff |= (source[srcOffset + 2] << D24) >>> D24;
        }

        switch (numSigBytes) {
            
            case D3:
                destination[ destOffset     ] = ALPHABET[ (inBuff >>> D18)        ];
                destination[ destOffset + 1 ] = ALPHABET[ (inBuff >>> D12) & F3 ];
                destination[ destOffset + 2 ] = ALPHABET[ (inBuff >>>  D6) & F3 ];
                destination[ destOffset + D3 ] = ALPHABET[ (inBuff) & F3 ];
                return destination;
                
            case 2:
                destination[ destOffset     ] = ALPHABET[ (inBuff >>> D18)        ];
                destination[ destOffset + 1 ] = ALPHABET[ (inBuff >>> D12) & F3 ];
                destination[ destOffset + 2 ] = ALPHABET[ (inBuff >>>  D6) & F3 ];
                destination[ destOffset + D3 ] = EQUALS_SIGN;
                return destination;
                
            case 1:
                destination[ destOffset     ] = ALPHABET[ (inBuff >>> D18)        ];
                destination[ destOffset + 1 ] = ALPHABET[ (inBuff >>> D12) & F3 ];
                destination[ destOffset + 2 ] = EQUALS_SIGN;
                destination[ destOffset + D3 ] = EQUALS_SIGN;
                return destination;
                
            default:
                return destination;
        }   // end switch
    }   // end encode3to4
    
    
    
    /**
     * Serializes an object and returns the Base64-encoded
     * version of that serialized object. If the object
     * cannot be serialized or there is another error,
     * the method will return <tt>null</tt>.
     * The object is not GZip-compressed before being encoded.
     *
     * @param serializableObject The object to encode
     * @return The Base64-encoded object
     * @since 1.4
     */
    public static String encodeObject(final java.io.Serializable serializableObject) {
        return encodeObject(serializableObject, NO_OPTIONS);
    }   // end encodeObject
    


    /**
     * Serializes an object and returns the Base64-encoded
     * version of that serialized object. If the object
     * cannot be serialized or there is another error,
     * the method will return <tt>null</tt>.
     * <p>
     * Valid options:<pre>
     *   GZIP: gzip-compresses object before encoding it.
     *   DONT_BREAK_LINES: don't break lines at 76 characters
     *     <i>Note: Technically, this makes your encoding non-compliant.</i>
     * </pre>
     * <p>
     * Example: <code>encodeObject( myObj, Base64.GZIP )</code> or
     * <p>
     * Example: <code>encodeObject( myObj, Base64.GZIP | Base64.DONT_BREAK_LINES )</code>
     *
     * @param serializableObject The object to encode
     * @param options Specified options
     * @return The Base64-encoded object
     * @see Base64#GZIP
     * @see Base64#DONT_BREAK_LINES
     * @since 2.0
     */
    public static String encodeObject(final java.io.Serializable serializableObject, final int options) {
        // Streams
        ByteArrayOutputStream  baos  = null; 
        OutputStream           b64os = null; 
        ObjectOutputStream     oos   = null; 
        GZIPOutputStream gzos  = null;
        
        // Isolate options
        int gzip           = (options & GZIP);
        int dontBreakLines = (options & DONT_BREAK_LINES);
        
        try {
            // ObjectOutputStream -> (GZIP) -> Base64 -> ByteArrayOutputStream
            baos  = new java.io.ByteArrayOutputStream();
            b64os = new Base64.OutputStream(baos, ENCODE | dontBreakLines);
    
            // GZip?
            if (gzip == GZIP) {
                gzos = new java.util.zip.GZIPOutputStream(b64os);
                oos  = new java.io.ObjectOutputStream(gzos);
            } else {
                oos  = new java.io.ObjectOutputStream(b64os);
            }
            
            oos.writeObject(serializableObject);
        } catch (java.io.IOException e) {
            //PlatformException.logError(e);
            return null;
        } finally {
            closeAllStreams(baos, b64os, oos,  gzos);
        }   // end finally
        
        // Return value according to relevant encoding.
        try {
            return new String(baos.toByteArray(), PREFERRED_ENCODING);
        } catch (java.io.UnsupportedEncodingException uue) {
            return String.valueOf(baos.toByteArray());
        }   // end catch
        
    }  // end encode
    
    

    /**
     * Encodes a byte array into Base64 notation.
     * Does not GZip-compress data.
     *
     * @param source The data to convert
     * @return The Base64-encoded bytes
     * @since 1.4
     */
    public static String encodeBytes(final byte[] source) {
        return encodeBytes(source, 0, source.length, NO_OPTIONS);
    }   // end encodeBytes
    


    /**
     * Encodes a byte array into Base64 notation.
     * <p>
     * Valid options:<pre>
     *   GZIP: gzip-compresses object before encoding it.
     *   DONT_BREAK_LINES: don't break lines at 76 characters
     *     <i>Note: Technically, this makes your encoding non-compliant.</i>
     * </pre>
     * <p>
     * Example: <code>encodeBytes( myData, Base64.GZIP )</code> or
     * <p>
     * Example: <code>encodeBytes( myData, Base64.GZIP | Base64.DONT_BREAK_LINES )</code>
     *
     *
     * @param source The data to convert
     * @param options Specified options
     * @return The Base64-encoded bytes
     * @see Base64#GZIP
     * @see Base64#DONT_BREAK_LINES
     * @since 2.0
     */
    public static String encodeBytes(final byte[] source, final int options) {   
        return encodeBytes(source, 0, source.length, options);
    }   // end encodeBytes
    
    
    /**
     * Encodes a byte array into Base64 notation.
     * Does not GZip-compress data.
     *
     * @param source The data to convert
     * @param off Offset in array where conversion should begin
     * @param len Length of data to convert
     * @return The Base64-encoded bytes
     * @since 1.4
     */
    public static String encodeBytes(final byte[] source, final int off, final int len) {
        return encodeBytes(source, off, len, NO_OPTIONS);
    }   // end encodeBytes
    
    /**
     * Compress the input data
     * 
     * @param source The data to convert
     * @param off Offset in array where conversion should begin
     * @param len Length of data to convert
     * @param dontBreakLines If dont break lines
     * @return The Base64-encoded and compressed bytes
     */
    private static String compress(final byte[] source, final int off, final int len, final int dontBreakLines) {
        java.io.ByteArrayOutputStream  baos  = null;
        java.util.zip.GZIPOutputStream gzos  = null;
        Base64.OutputStream            b64os = null;        

        try {
            // GZip -> Base64 -> ByteArray
            baos = new java.io.ByteArrayOutputStream();
            b64os = new Base64.OutputStream(baos, ENCODE | dontBreakLines);
            gzos  = new java.util.zip.GZIPOutputStream(b64os); 
        
            gzos.write(source, off, len);
        } catch (java.io.IOException e) {
            //PlatformException.logError(e);
            return null;
        } finally {
            try {
                gzos.close();
            } catch (java.io.IOException ioe) {
                return "";
            }
        }   // end finally

        // Return value according to relevant encoding.
        try {
            return new String(baos.toByteArray(), PREFERRED_ENCODING);
        } catch (java.io.UnsupportedEncodingException uue) {
            return String.valueOf(baos.toByteArray());
        }   // end catch
    }
    

    /**
     * Encodes a byte array into Base64 notation.
     * <p>
     * Valid options:<pre>
     *   GZIP: gzip-compresses object before encoding it.
     *   DONT_BREAK_LINES: don't break lines at 76 characters
     *     <i>Note: Technically, this makes your encoding non-compliant.</i>
     * </pre>
     * <p>
     * Example: <code>encodeBytes( myData, Base64.GZIP )</code> or
     * <p>
     * Example: <code>encodeBytes( myData, Base64.GZIP | Base64.DONT_BREAK_LINES )</code>
     *
     *
     * @param source The data to convert
     * @param off Offset in array where conversion should begin
     * @param len Length of data to convert
     * @param options Specified options
     * @return The Base64-encoded bytes
     * @see Base64#GZIP
     * @see Base64#DONT_BREAK_LINES
     * @since 2.0
     */
    public static String encodeBytes(final byte[] source, final int off, final int len, final int options) {
        // Isolate options
        int dontBreakLines = (options & DONT_BREAK_LINES);
        int gzip           = (options & GZIP);
        
        // Compress?
        if (gzip == GZIP) {
            return compress(source, off, len, dontBreakLines);           
        }  // end if: compress
        
        // Else, don't compress. Better not to use streams at all then.
        
        // Convert option to boolean in way that code likes it.
        boolean breakLines = (dontBreakLines == 0);
        
        int len43 = len * D4 / D3;
        int pos = len43; // Main 4:3
        if ((len % D3) > 0) {
            pos += D4; // Account for padding
        }
        if (breakLines) {
            pos += len43 / MAX_LINE_LENGTH; // New lines
        }
        byte[] outBuff = new byte[ pos ];       
        int d = 0;
        int e = 0;
        int len2 = len - 2;
        int lineLength = 0;
        for ( ; d < len2; d += D3, e += D4) {
            encode3to4(source, d + off, D3, outBuff, e);

            lineLength += D4;
            if (breakLines && lineLength == MAX_LINE_LENGTH) {   
                outBuff[e + D4] = NEW_LINE;
                e++;
                lineLength = 0;
            }   // end if: end of line
        }   // en dfor: each piece of array

        if (d < len) {
            encode3to4(source, d + off, len - d, outBuff, e);
            e += D4;
        }   // end if: some padding needed

        
        // Return value according to relevant encoding.
        try {
            return new String(outBuff, 0, e, PREFERRED_ENCODING);
        } catch (java.io.UnsupportedEncodingException uue) {
            return new String(outBuff, 0, e);
        }   // end catch
    	// end else: don't compress
        
    }   // end encodeBytes
    

    
    
    
/* ********  D E C O D I N G   M E T H O D S  ******** */
    
    
    /**
     * Decodes four bytes from array <var>source</var>
     * and writes the resulting bytes (up to three of them)
     * to <var>destination</var>.
     * The source and destination arrays can be manipulated
     * anywhere along their length by specifying 
     * <var>srcOffset</var> and <var>destOffset</var>.
     * This method does not check to make sure your arrays
     * are large enough to accomodate <var>srcOffset</var> + 4 for
     * the <var>source</var> array or <var>destOffset</var> + 3 for
     * the <var>destination</var> array.
     * This method returns the actual number of bytes that 
     * were converted from the Base64 encoding.
     * 
     *
     * @param source the array to convert
     * @param srcOffset the index where conversion begins
     * @param destination the array to hold the conversion
     * @param destOffset the index where output will be put
     * @return the number of decoded bytes converted
     * @since 1.3
     */
    private static int decode4to3(
     final byte[] source, final int srcOffset,
     final byte[] destination, final int destOffset) {
        // Example: Dk==
        if (source[ srcOffset + 2] == EQUALS_SIGN) {
            // Two ways to do the same thing. Don't know which way I like best.
            //int outBuff =   ((DECODABET[ source[ srcOffset    ] ] << D24) >>>  D6)
            //              | ((DECODABET[ source[ srcOffset + 1] ] << D24) >>> D12);
            int outBuff =   ((DECODABET[ source[ srcOffset    ] ] & H_00FF) << D18)
                          | ((DECODABET[ source[ srcOffset + 1] ] & H_00FF) << D12);
            
            destination[ destOffset ] = (byte) (outBuff >>> D16);
            return 1;
        }
        
        // Example: DkL=
        if (source[ srcOffset + D3 ] == EQUALS_SIGN) {
            // Two ways to do the same thing. Don't know which way I like best.
            //int outBuff =   ((DECODABET[ source[ srcOffset     ] ] << D24) >>>  D6)
            //              | ((DECODABET[ source[ srcOffset + 1 ] ] << D24) >>> D12)
            //              | ((DECODABET[ source[ srcOffset + 2 ] ] << D24) >>> D18);
            int outBuff =   ((DECODABET[ source[ srcOffset     ] ] & H_00FF) << D18)
                          | ((DECODABET[ source[ srcOffset + 1 ] ] & H_00FF) << D12)
                          | ((DECODABET[ source[ srcOffset + 2 ] ] & H_00FF) <<  D6);
            
            destination[ destOffset     ] = (byte) (outBuff >>> D16);
            destination[ destOffset + 1 ] = (byte) (outBuff >>>  D8);
            return 2;
        }
        
        // Example: DkLE
        try {
            // Two ways to do the same thing. Don't know which way I like best.
            //int outBuff =   ((DECODABET[ source[ srcOffset     ] ] << D24) >>>  D6)
            //              | ((DECODABET[ source[ srcOffset + 1 ] ] << D24) >>> D12)
            //              | ((DECODABET[ source[ srcOffset + 2 ] ] << D24) >>> D18)
            //              | ((DECODABET[ source[ srcOffset + 3 ] ] << D24) >>> D24);
            int outBuff =   ((DECODABET[ source[ srcOffset     ] ] & H_00FF) << D18)
                          | ((DECODABET[ source[ srcOffset + 1 ] ] & H_00FF) << D12)
                          | ((DECODABET[ source[ srcOffset + 2 ] ] & H_00FF) <<  D6)
                          | ((DECODABET[ source[ srcOffset + D3 ] ] & H_00FF));

            
            destination[ destOffset     ] = (byte) (outBuff >> D16);
            destination[ destOffset + 1 ] = (byte) (outBuff >>  D8);
            destination[ destOffset + 2 ] = (byte) (outBuff       );

            return D3;
        } catch (Exception e) {
            /*logger.debug("" + source[srcOffset] +  ": " + (DECODABET[ source[ srcOffset     ] ]));
            logger.debug("" + source[srcOffset + 1] +  ": " + (DECODABET[ source[ srcOffset + 1 ] ]));
            logger.debug("" + source[srcOffset + 2] +  ": " + (DECODABET[ source[ srcOffset + 2 ] ]));
            logger.debug("" + source[srcOffset + D3] +  ": " + (DECODABET[ source[ srcOffset + D3 ] ]));*/
            return -1;
        }   //end catch
    }   // end decodeToBytes
    
    
    
    
    /**
     * Very low-level access to decoding ASCII characters in
     * the form of a byte array. Does not support automatically
     * gunzipping or any other "fancy" features.
     *
     * @param source The Base64 encoded data
     * @param off    The offset of where to begin decoding
     * @param len    The length of characters to decode
     * @return decoded data
     * @since 1.3
     */
    public static byte[] decode(final byte[] source, final int off, final int len) {
        int    len34   = len * D3 / D4;
        byte[] outBuff = new byte[ len34 ]; // Upper limit on size of output
        int    outBuffPosn = 0;
        
        byte[] b4        = new byte[D4];
        int    b4Posn    = 0;
        int    i         = 0;
        byte   sbiCrop   = 0;
        byte   sbiDecode = 0;
        for (i = off; i < (off + len); i++) {
            sbiCrop = (byte) (source[i] & H_007F); // Only the low seven bits
            sbiDecode = DECODABET[ sbiCrop ];
            
            if (sbiDecode >= WHITE_SPACE_ENC) { // White space, Equals sign or better
                if (sbiDecode >= EQUALS_SIGN_ENC) {
                    b4[ b4Posn++ ] = sbiCrop;
                    if (b4Posn > D3) {
                        outBuffPosn += decode4to3(b4, 0, outBuff, outBuffPosn);
                        b4Posn = 0;
                        
                        // If that was the equals sign, break out of 'for' loop
                        if (sbiCrop == EQUALS_SIGN) {
                            break;
                        }
                    }   // end if: quartet built                    
                }   // end if: equals sign or better                
            } else {
                //System.err.println( "Bad Base64 input character at " + i + ": " + source[i] + "(decimal)" );
                return null;
            }   // end else: 
        }   // each input character
                                   
        byte[] out = new byte[ outBuffPosn ];
        System.arraycopy(outBuff, 0, out, 0, outBuffPosn); 
        return out;
    }   // end decode
    
    
    
    
    /**
     * Decodes data from Base64 notation, automatically
     * detecting gzip-compressed data and decompressing it.
     *
     * @param s the string to decode
     * @return the decoded data
     * @since 1.4
     */
    public static byte[] decode(final String s) {   
        byte[] bytes;
        try {
            bytes = s.getBytes(PREFERRED_ENCODING);
        } catch (java.io.UnsupportedEncodingException uee) {
            bytes = s.getBytes();
        }   // end catch
		//</change>
        
        // Decode
        bytes = decode(bytes, 0, bytes.length);
        
        
        // Check to see if it's gzip-compressed
        // GZIP Magic Two-Byte Number: 0x8b1f (35615)
        if (bytes != null && bytes.length >= D4) {
            
            int head = (bytes[0] & H_00FF) | ((bytes[1] << D8) & H_FF00);       
            if (java.util.zip.GZIPInputStream.GZIP_MAGIC == head) {
                bytes = decompress(bytes);
            }   // end if: gzipped
        }   // end if: bytes.length >= 2
        
        return bytes;
    }   // end decode

    /**
     * Decompress input data.
     * 
     * @param bytes The compressed input data
     * @return The decompressed data
     */
    private static byte[] decompress(final byte[] bytes) {
        java.io.ByteArrayInputStream  bais = null;
        java.util.zip.GZIPInputStream gzis = null;
        java.io.ByteArrayOutputStream baos = null;
        byte[] buffer = new byte[BUFFER_SIZE];
        int    length = 0;

        try {
            baos = new java.io.ByteArrayOutputStream();
            bais = new java.io.ByteArrayInputStream(bytes);
            gzis = new java.util.zip.GZIPInputStream(bais);

            while ((length = gzis.read(buffer)) >= 0) {
                baos.write(buffer, 0, length);
            }   // end while: reading input
            
        } catch (java.io.IOException e) {                    
            // Just return originally-decoded bytes
            return bytes;
        }  finally {
            try {
                baos.close();
                gzis.close();
                bais.close();
            } catch (java.io.IOException ioe) {
                return bytes;
            }
        }   // end finally
        
        //No error? Get new bytes.
        return baos.toByteArray();        
    }
    

    /**
     * Attempts to decode Base64 data and deserialize a Java
     * Object within. Returns <tt>null</tt> if there was an error.
     *
     * @param encodedObject The Base64 data to decode
     * @return The decoded and deserialized object
     * @since 1.5
     */
    public static Object decodeToObject(final String encodedObject) {
        // Decode and gunzip if necessary
        byte[] objBytes = decode(encodedObject);
        
        java.io.ByteArrayInputStream  bais = null;
        java.io.ObjectInputStream     ois  = null;
        Object obj = null;
        
        try {
            bais = new java.io.ByteArrayInputStream(objBytes);
            ois  = new java.io.ObjectInputStream(bais);
        
            obj = ois.readObject();
        } catch (java.io.IOException e) {
            //PlatformException.logError(e);
            obj = null;
        } catch (java.lang.ClassNotFoundException e) {
            //PlatformException.logError(e);
            obj = null;
        } finally {
            try { bais.close(); } catch (Exception e) { doNoAction(); }
            try { ois.close();  } catch (Exception e) { doNoAction(); }
        }   // end finally
        
        return obj;
    }  // end decodeObject
    
    /**
     * This method does no action at all.
     */
    private static void doNoAction() {
        
    }
    
    /**
     * Tries to close all output streams, catching all the exceptions.
     * 
     * @param baos The ByteArrayOutputStream
     * @param b64os The Base64.OutputStream
     * @param oos The ObjectOutputStream
     * @param gzos The GZIPOutputStream
     */
    private static void closeAllStreams(final ByteArrayOutputStream baos,
            final OutputStream b64os, final ObjectOutputStream oos, final GZIPOutputStream gzos) {

        try { oos.close();   } catch (Exception e) { doNoAction(); }
        try { gzos.close();  } catch (Exception e) { doNoAction(); }
        try { b64os.close(); } catch (Exception e) { doNoAction(); }
        try { baos.close();  } catch (Exception e) { doNoAction(); }

    }
    /**
     * Convenience method for encoding data to a file.
     *
     * @param dataToEncode byte array of data to encode in base64 form
     * @param filename Filename for saving encoded data
     * @return <tt>true</tt> if successful, <tt>false</tt> otherwise
     *
     * @since 2.1
     *//*
    public static boolean encodeToFile(final byte[] dataToEncode, final String filename) {
        boolean success = false;
        Base64.OutputStream bos = null;
        try {
            bos = new Base64.OutputStream(
                      new java.io.FileOutputStream(filename), Base64.ENCODE);
            bos.write(dataToEncode);
            success = true;
        } catch (java.io.IOException e) {            
            success = false;
        } finally {
            try { bos.close(); } catch (Exception e) { }
        }   // end finally
        
        return success;
    } */  // end encodeToFile
    
    
    /**
     * Convenience method for decoding data to a file.
     *
     * @param dataToDecode Base64-encoded data as a string
     * @param filename Filename for saving decoded data
     * @return <tt>true</tt> if successful, <tt>false</tt> otherwise
     *
     * @since 2.1
     */
    /*public static boolean decodeToFile(final String dataToDecode, final String filename) {
        boolean success = false;
        Base64.OutputStream bos = null;
        try {
                bos = new Base64.OutputStream(
                          new java.io.FileOutputStream(filename), Base64.DECODE);
                bos.write(dataToDecode.getBytes( PREFERRED_ENCODING ) );
                success = true;
        }   // end try
        catch( java.io.IOException e )
        {
            success = false;
        }   // end catch: IOException
        finally
        {
                try{ bos.close(); } catch( Exception e ){}
        }   // end finally
        
        return success;
    } */  // end decodeToFile
    
    
    
    
    /**
     * Convenience method for reading a base64-encoded
     * file and decoding it.
     *
     * @param filename Filename for reading encoded data
     * @return decoded byte array or null if unsuccessful
     *
     * @since 2.1
     */
    /*
    public static byte[] decodeFromFile(final String filename) {
        byte[] decodedData = null;
        Base64.InputStream bis = null;
        try {
            // Set up some useful variables
            java.io.File file = new java.io.File(filename);
            byte[] buffer = null;
            int length   = 0;
            int numBytes = 0;
            
            // Check for size of file
            if (file.length() > Integer.MAX_VALUE) {
                //System.err.println( "File is too big for this convenience method (" + file.length() + " bytes)." );
                return null;
            }   // end if: file too big for int index
            buffer = new byte[ (int)file.length() ];
            
            // Open a stream
            bis = new Base64.InputStream( 
                      new java.io.BufferedInputStream( 
                      new java.io.FileInputStream( file ) ), Base64.DECODE );
            
            // Read until done
            while( ( numBytes = bis.read( buffer, length, 4096 ) ) >= 0 )
                length += numBytes;
            
            // Save in a variable to return
            decodedData = new byte[ length ];
            System.arraycopy( buffer, 0, decodedData, 0, length );
            
        }   // end try
        catch( java.io.IOException e )
        {
            //System.err.println( "Error decoding from file " + filename );
        }   // end catch: IOException
        finally
        {
            try{ bis.close(); } catch( Exception e) {}
        }   // end finally
        
        return decodedData;
    }*/   // end decodeFromFile
    
    
    
    /**
     * Convenience method for reading a binary file
     * and base64-encoding it.
     *
     * @param filename Filename for reading binary data
     * @return base64-encoded string or null if unsuccessful
     *
     * @since 2.1
     */
    /*public static String encodeFromFile( String filename )
    {
        String encodedData = null;
        Base64.InputStream bis = null;
        try
        {
            // Set up some useful variables
            java.io.File file = new java.io.File( filename );
            byte[] buffer = new byte[ (int)(file.length() * 1.4) ];
            int length   = 0;
            int numBytes = 0;
            
            // Open a stream
            bis = new Base64.InputStream( 
                      new java.io.BufferedInputStream( 
                      new java.io.FileInputStream( file ) ), Base64.ENCODE );
            
            // Read until done
            while( ( numBytes = bis.read( buffer, length, 4096 ) ) >= 0 )
                length += numBytes;
            
            // Save in a variable to return
            encodedData = String.valueOf( buffer, 0, length, Base64.PREFERRED_ENCODING );
                
        }   // end try
        catch( java.io.IOException e )
        {
            //System.err.println( "Error encoding from file " + filename );
        }   // end catch: IOException
        finally
        {
            try{ bis.close(); } catch( Exception e) {}
        }   // end finally
        
        return encodedData;
        }*/   // end encodeFromFile
    
    
    
        
    
    
    /* ********  I N N E R   C L A S S   O U T P U T S T R E A M  ******** */
    
    
    
    /**
     * A {@link Base64.OutputStream} will write data to another
     * <tt>java.io.OutputStream</tt>, given in the constructor,
     * and encode/decode to/from Base64 notation on the fly.
     *
     * @see Base64
     * @since 1.3
     */
    public static class OutputStream extends java.io.FilterOutputStream {
		/**
		 * Comentario para <code>encode</code>
		 */
        private boolean encode;
		/**
		 * Comentario para <code>position</code>
		 */
        private int     position;
		/**
		 * Comentario para <code>buffer</code>
		 */
        private byte[]  buffer;
		/**
		 * Comentario para <code>bufferLength</code>
		 */
        private int     bufferLength;
		/**
		 * Comentario para <code>lineLength</code>
		 */
        private int     lineLength;
		/**
		 * Comentario para <code>breakLines</code>
		 */
        private boolean breakLines;
		/**
		 * Comentario para <code>b4</code>
		 */
        private byte[]  b4; // Scratch used in a few places
        
        /**
         * Constructs a {@link Base64.OutputStream} in ENCODE mode.
         *
         * @param out the <tt>java.io.OutputStream</tt> to which data will be written.
         * @since 1.3
         */
        public OutputStream(final java.io.OutputStream out) {   
            this(out, ENCODE);
        }   // end constructor
        
        
        /**
         * Constructs a {@link Base64.OutputStream} in
         * either ENCODE or DECODE mode.
         * <p>
         * Valid options:<pre>
         *   ENCODE or DECODE: Encode or Decode as data is read.
         *   DONT_BREAK_LINES: don't break lines at 76 characters
         *     (only meaningful when encoding)
         *     <i>Note: Technically, this makes your encoding non-compliant.</i>
         * </pre>
         * <p>
         * Example: <code>new Base64.OutputStream( out, Base64.ENCODE )</code>
         *
         * @param out the <tt>java.io.OutputStream</tt> to which data will be written.
         * @param options Specified options.
         * @see Base64#ENCODE
         * @see Base64#DECODE
         * @see Base64#DONT_BREAK_LINES
         * @since 1.3
         */
        public OutputStream(final java.io.OutputStream out, final int options) {   
            super(out);
            this.breakLines   = (options & DONT_BREAK_LINES) != DONT_BREAK_LINES;
            this.encode       = (options & ENCODE) == ENCODE;
            if (encode) {
                this.bufferLength = D3;
            } else {
                this.bufferLength = D4;
            }
            this.buffer       = new byte[ bufferLength ];
            this.position     = 0;
            this.lineLength   = 0;
            this.b4           = new byte[D4];
        }   // end constructor
        
        
        /**
         * Writes the byte to the output stream after
         * converting to/from Base64 notation.
         * When encoding, bytes are buffered three
         * at a time before the output stream actually
         * gets a write() call.
         * When decoding, bytes are buffered four
         * at a time.
         *
         * @param theByte the byte to write
         * @throws java.io.IOException When there is an invalid character in Base64 data.
         * @since 1.3
         */
        public void write(final int theByte) throws java.io.IOException {
            // Encode?
            if (encode) {
                buffer[ position++ ] = (byte) theByte;
                if (position >= bufferLength) { // Enough to encode.
                    out.write(encode3to4(b4, buffer, bufferLength));

                    lineLength += D4;
                    if (breakLines && lineLength >= MAX_LINE_LENGTH) {
                        out.write(NEW_LINE);
                        lineLength = 0;
                    }   // end if: end of line

                    position = 0;
                }   // end if: enough to output
            } else {
                // Meaningful Base64 character?
                if (DECODABET[ theByte & H_007F ] > WHITE_SPACE_ENC) {
                    buffer[ position++ ] = (byte) theByte;
                    if (position >= bufferLength) { // Enough to output.
                        int len = Base64.decode4to3(buffer, 0, b4, 0);
                        out.write(b4, 0, len);
                        //out.write( Base64.decode4to3( buffer ) );
                        position = 0;
                    }   // end if: enough to output
                } else {
                    if (DECODABET[ theByte & H_007F ] != WHITE_SPACE_ENC) {
	                    throw new java.io.IOException("Invalid character in Base64 data.");
                    }
	            }   // end else: not white space either
            }   // end else: decoding
        }   // end write
        
        
        
        /**
         * Calls {@link #write(int)} repeatedly until <var>len</var> 
         * bytes are written.
         *
         * @param theBytes array from which to read bytes
         * @param off offset for array
         * @param len max number of bytes to read into array
         * @throws java.io.IOException When there is an invalid character in Base64 data.
         * @since 1.3
         */
        public void write(final byte[] theBytes, final int off, final int len) throws java.io.IOException {           
            for (int i = 0; i < len; i++) {
                write(theBytes[ off + i ]);
            }   // end for: each byte written
            
        }   // end write
        
        
        
        /**
         * Method added by PHIL. [Thanks, PHIL. -Rob]
         * This pads the buffer without closing the stream.
         * 
         * @throws java.io.IOException When base64 input not properly padded.
         */
        public void flushBase64() throws java.io.IOException  {
            if (position > 0) {
                if (encode)  {
                    out.write(encode3to4(b4, buffer, position));
                    position = 0;
                } else {
                    throw new java.io.IOException("Base64 input not properly padded.");
                }   // end else: decoding
            }   // end if: buffer partially full

        }   // end flush

        
        /** 
         * Flushes and closes (I think, in the superclass) the stream. 
         *
         * @throws java.io.IOException When base64 input not properly padded.
         * @since 1.3
         */
        public void close() throws java.io.IOException {
            // 1. Ensure that pending characters are written
            flushBase64();

            // 2. Actually close the stream
            // Base class both flushes and closes.
            super.close();
            
            buffer = null;
            out    = null;
        }   // end close
    }   // end inner class OutputStream
        
}   // end class Base64




