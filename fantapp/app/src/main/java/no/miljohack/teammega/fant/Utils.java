package no.miljohack.teammega.fant;

class Utils {

    private static final String HEX_CHARS = "0123456789ABCDEF";

    static byte[] hexStringToByteArray(String data){
        byte[] result = new byte[data.length()/2];
        for (int i = 0; i < data.length(); i+=2) {
            int firstIndex = HEX_CHARS.indexOf(data.charAt(i));
            int secondIndex = HEX_CHARS.indexOf(data.charAt(i+1));

            int octet = Integer.rotateLeft(firstIndex, 4)|secondIndex;
            result[Integer.rotateRight(i, 1)] = (byte) octet;
        }
        return result;
    }

    static String toHex(byte[] byteArray){
        StringBuffer result = new StringBuffer();
        for (byte b : byteArray) {
            byte octet = b;
            int firstIndex = (octet & 240) >>> 4;
            int secondIndex = (octet & 15);
            result.append(HEX_CHARS.charAt(firstIndex));
            result.append(HEX_CHARS.charAt(secondIndex));
        }
        return result.toString();
    }
}