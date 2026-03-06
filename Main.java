import java.time.LocalTime;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main {
	public static void main(String[] args){
		LocalTime now = LocalTime.now();
		int hour = now.getHour();
		int min = now.getMinute();
		System.out.println(now  + "  " + hour + "  " + min);

	}
}
