package logic;
import com.google.common.primitives.Longs;


public class JSONUtil {

	
	public static byte jsonObjToByte(Object obj) {
		byte[] bytes = Longs.toByteArray((long) obj);
		return bytes[(Long.SIZE / 8) - 1];
	}
	
	public static int jsonObjToInt(Object obj) {
		int result = (int) ((long) obj);
		return result;
	}
}
