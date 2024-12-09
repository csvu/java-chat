package mop.app.client.dao.user;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class SqlReader {
    Scanner inp;
    public SqlReader(String file) {
        try {
            inp = new Scanner(new File(file));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public String read() {
        StringBuilder sb = new StringBuilder();
        while(inp.hasNextLine()) {
            sb.append(inp.nextLine()).append("\n");
        }
        return sb.toString();
    }
}
