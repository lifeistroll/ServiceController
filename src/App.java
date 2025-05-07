import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        System.out.println("choose:");
        System.out.println("1. start");
        System.out.println("2. stop");
        System.out.println("3. check");

        int user_choose = scanner.nextInt();
        scanner.close();

        try {
            switch (user_choose) {
                case 1:
                    executeCommand(new String[] { "cmd.exe", "/c", "net", "start", "BcastDVRUserService_e27cf" });
                    break;
                case 2:
                    executeCommand(new String[] { "cmd.exe", "/c", "net", "stop", "BcastDVRUserService_e27cf" });
                    break;
                case 3:
                    executeCommand(new String[] { "cmd.exe", "/c", "sc", "query", "BcastDVRUserService_e27cf" });
                    break;
                default:
                    System.out.println("choooooose");
            }
        } catch (Exception e) {
            System.err.println("error: " + e.getMessage());
        }
    }

    private static void executeCommand(String[] command) throws IOException, InterruptedException {
        Process process = Runtime.getRuntime().exec(command);

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            System.err.println("errot " + exitCode);
        }
    }

}
