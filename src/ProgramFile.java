import java.io.*;

public class ProgramFile {

    private String program;

    public ProgramFile(String fileName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(new File(fileName)))) {

            String line;
            StringBuilder builder = new StringBuilder();
            while((line = reader.readLine()) != null){
                builder.append(" ").append(line);
            }

            this.program = builder.toString();
        }
        catch (IOException ex){
            ex.printStackTrace();
        }
    }

    public String getProgram() {
        return program;
    }
}
