import java.io.IOException;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        String[] service_start = { "cmd.exe", "/c", "sc", "start", "SERVICE_NAME" };

        String[] service_stop = { "cmd.exe", "/c", "sc", "stop", "SERVICE_NAME" };

        String[] service_check = { "cmd.exe", "/c", "sc", "query", "SERVICE_NAME", "|", "find", "/C",
                "\"RUNNING\"" };

        int user_choose = scanner.nextInt();

        if (user_choose == 1) {
            try {
                Runtime.getRuntime().exec(service_start);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (user_choose == 2) {
            try {
                Runtime.getRuntime().exec(service_stop);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (user_choose == 3) {
            try {
                Runtime.getRuntime().exec(service_check);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("choose");
        }

    }

}
