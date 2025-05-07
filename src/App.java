import java.io.IOException;

public class App {
    public static void main(String[] args) {

        String[] service_start = { "cmd.exe", "/c", "sc", "start", "SERVICE_NAME" };

        String[] service_stop = { "cmd.exe", "/c", "sc", "stop", "SERVICE_NAME" };

        String[] service_check = { "cmd.exe", "/c", "sc", "query", "SERVICE_NAME", "|", "find", "/C",
                "\"RUNNING\"" };

        try {
            Process process = Runtime.getRuntime().exec(service_start);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
