import java.io.*;

public class Project1_Digits {
   public static void main(String[] args) {
        if (args.length != 4) {
            System.err.println("Usage: java HexConverter <input_file> <l/b> <i/u/f> <size>");
            return;
        }

        String inputFile = args[0];
        String mid = args[1];
        char dataType = args[2].charAt(0);
        int sizetype = Integer.parseInt(args[3]);
        System.out.println(sizetype);

        try (BufferedReader br = new BufferedReader(new FileReader(inputFile));
             PrintWriter pw = new PrintWriter(new FileWriter("output.txt"))) {
            String line;

            while ((line = br.readLine()) != null) {
            	
                  char[][] dum = read(mid, line, sizetype);
                  for(int i = 0; i<dum.length; i++) {
	                  byte[] dum2 = convert(dum[i]);
	                  String result;
	                  System.out.println(line);
	                  
	                  for(int z = 0; z< dum[i].length; z++) {
	                	  System.out.print(dum[i][z]);
	                  }

	                  System.out.println();
	                  for(int j=0; j<dum2.length;j++) {
	                	  System.out.print(dum2[j]);
	                  }
	                  System.out.println();
                  
	                  if (dataType == 'i') {
	                      result = String.valueOf(convertToSignedInt(dum2));
	                  } else if (dataType == 'u') {
	                      result = String.valueOf(convertToUnsignedInt(dum2));
	                  } else if (dataType == 'f') {
	                      result = formatFloatingPoint(dum2, sizetype);
	                  } else {
	                      throw new IllegalArgumentException("Invalid data type: " + dataType);
	                  }
	                 System.out.println(result);
	                  pw.print(result);
	                  pw.print(" ");

                  }
                  pw.println();
                   
                
            }

        } catch (IOException | IllegalArgumentException e) {
            System.err.println(e.getMessage());
        }
    }

	
	


    private static int convertToSignedInt(byte[] bytes) {
        int value = 0;
        if(bytes[0]==1){
            value += Math.pow(2, bytes.length-1) * -1; 
            for (int i = 1; i < bytes.length; i++) {
                if(bytes[i]==1)
                value += Math.pow(2, bytes.length - i - 1);
            }
        }
        else{
            for (int i = 1; i < bytes.length; i++) {
                if(bytes[i]==1){                           
                value += Math.pow(2, bytes.length - i - 1);
            }

            }
        }
        return value;
    }

    private static long convertToUnsignedInt(byte[] bytes) {
        long value = 0;
        for (int i = 0; i < bytes.length; i++) {
           value = value <<1;
        	value |= (bytes[i] & 0xFFL);
            
        }
        return value;
    }
    
    private static String formatFloatingPoint(byte[] bytes, int size) {
    	// Determine the number of bits for the exponent based on the size
        int exponentBits;
        switch (size) {
        	case 1: exponentBits = 4; break;
        	case 2: exponentBits = 6; break;
        	case 3: exponentBits = 8; break;
        	case 4: exponentBits = 10; break;
            default: throw new IllegalArgumentException("Unsupported size for floating point: " + size);
        }
        System.out.println(size);
        int fractionBits = (8 * size) - exponentBits - 1; // Calculate the number of fraction bits
        // Combine the bytes into a single integer (raw representation)
        int raw = 0;
        for (int i = 0; i < size*8; i++) {
        	raw = raw<<1;
            raw |= (bytes[i] & 0xFF); // Shift and combine bytes
        }
        int sign = (raw >>> (size * 8 -1)) & 1;//Get sign bit
        int  exponent = (raw >>> fractionBits)  & ((1<<exponentBits)-1);//Extract exponent bits
        int fraction = raw & ((1 << fractionBits) - 1); // Extract fraction bits
        // Calculate the bias for the exponent
        int bias = (1 << (exponentBits - 1)) - 1;
        // Initialize the mantissa
        double mantissa = (exponent == 0) ? 0.0 : 1.0; // Start with 1.0 for normalized values

        // Calculate the mantissa from the fraction bits (only the first 13 bits)
        for (int i = 0; i < Math.min(fractionBits, 13); i++) {
            if ((fraction & (1 << (fractionBits - 1 - i))) != 0) {
                mantissa += Math.pow(2, -(i + 1)); // Add the value of the fraction bit
                System.out.println(fraction);
            }
        }
 
        // Rounding to even for the 13th bit
        if (fractionBits > 13) {
            // Check if the 14th bit is set (to determine rounding)
            boolean isFourteenthBitSet = (fraction & (1 << (fractionBits - 14))) != 0;
            boolean isOdd = ((fraction >> (fractionBits - 13)) & 1) == 1; // Check if the 13th bit is odd

            // If the 14th bit is set and the 13th bit is odd, we round up
            if (isFourteenthBitSet && isOdd) {
                mantissa += Math.pow(2, -13); // Round up
            }
            
            else if(isFourteenthBitSet) {
            	for(int i = 0; i < fractionBits - 13; i++) {
            		
            		if( (fraction & (1 << ( fractionBits - 15 -i) ) ) != 0) {
            			System.out.println(fractionBits);
            			System.out.println("aaaaaaaaaaaaaaaaaaaaaaaa ");
            			System.out.println(mantissa);
                        mantissa += Math.pow(2, -13); // Round up
                        break;
            		}
            		
            	}
            }
        }
        // Calculate the final floating-point value
        double value;
        if (exponent == (1 << exponentBits) - 1) {
        //Handle the infinite and NaN NUMBERS
            value = (fraction == 0) ? (sign == 1 ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY) : Double.NaN;
        } else if (exponent == 0) {
          //Handle denormalized numbers
            value = Math.pow(-1, sign) * mantissa * Math.pow(2, 1 - bias);
        } else {
          //Handle normalized numbers 
            value = Math.pow(-1, sign) * mantissa * Math.pow(2, exponent - bias);
        }
        if(value==0){
            if(sign==1)
            return "-0";
            else
            return "0";
        }
        // Format the output to a maximum of 6 significant digits
        return String.format("%.6g", value);
    	}

        public static char[][] read(String middle,String s, int size) {
            s = s.replace(" ", "");
            char[][] sa = new char[12/size][size*2];
            
            if(middle.equals("b")) {
                int indx = 0;
                
                for(int i = 0; i < 12/size; i++) {
                    for(int j = 0 ; j < size*2; j+=2) {
                        sa[i][j] = s.charAt(indx++);
                        sa[i][j+1] = s.charAt(indx++);
                    }
                }
                
            }
            
            else if(middle.equals("l")) {
    
                for(int i = 0; i < 12/size; i++) {
                    int indx = (size*2)*(i+1) - 1;
                    for(int j = 0; j < size*2; j+=2) {
                        
                        sa[i][j+1] = s.charAt(indx);
                        sa[i][j] = s.charAt(indx-1);
                        indx-=2;
                    }
                }
    
                
                
            }
                    
            return sa;
            
        }
	public static  byte[] convert(char[] data){
        byte[] result = new byte[data.length*4];
        for(int i=0;i<data.length;i++){
            switch (Character.toUpperCase(data[i])) {
                case '0': result[4 * i] = 0; result[4 * i + 1] = 0; result[4 * i + 2] = 0; result[4 * i + 3] = 0; break;
                case '1': result[4 * i] = 0; result[4 * i + 1] = 0; result[4 * i + 2] = 0; result[4 * i + 3] = 1; break;
                case '2': result[4 * i] = 0; result[4 * i + 1] = 0; result[4 * i + 2] = 1; result[4 * i + 3] = 0; break;
                case '3': result[4 * i] = 0; result[4 * i + 1] = 0; result[4 * i + 2] = 1; result[4 * i + 3] = 1; break;
                case '4': result[4 * i] = 0; result[4 * i + 1] = 1; result[4 * i + 2] = 0; result[4 * i + 3] = 0; break;
                case '5': result[4 * i] = 0; result[4 * i + 1] = 1; result[4 * i + 2] = 0; result[4 * i + 3] = 1; break;
                case '6': result[4 * i] = 0; result[4 * i + 1] = 1; result[4 * i + 2] = 1; result[4 * i + 3] = 0; break;
                case '7': result[4 * i] = 0; result[4 * i + 1] = 1; result[4 * i + 2] = 1; result[4 * i + 3] = 1; break;
                case '8': result[4 * i] = 1; result[4 * i + 1] = 0; result[4 * i + 2] = 0; result[4 * i + 3] = 0; break;
                case '9': result[4 * i] = 1; result[4 * i + 1] = 0; result[4 * i + 2] = 0; result[4 * i + 3] = 1; break;
                case 'A': result[4 * i] = 1; result[4 * i + 1] = 0; result[4 * i + 2] = 1; result[4 * i + 3] = 0; break;
                case 'B': result[4 * i] = 1; result[4 * i + 1] = 0; result[4 * i + 2] = 1; result[4 * i + 3] = 1; break;
                case 'C': result[4 * i] = 1; result[4 * i + 1] = 1; result[4 * i + 2] = 0; result[4 * i + 3] = 0; break;
                case 'D': result[4 * i] = 1; result[4 * i + 1] = 1; result[4 * i + 2] = 0; result[4 * i + 3] = 1; break;
                case 'E': result[4 * i] = 1; result[4 * i + 1] = 1; result[4 * i + 2] = 1; result[4 * i + 3] = 0; break;
                case 'F': result[4 * i] = 1; result[4 * i + 1] = 1; result[4 * i + 2] = 1; result[4 * i + 3] = 1; break;
            }
        }
        return result; 
    }
}