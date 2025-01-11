package pro.sky.telegrambot.fileservice;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.springframework.stereotype.Service;

@Service
public class FileSaveService {

    public File saveFile(byte[] data) throws IOException {
        File file = new File("categories.xlsx");
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(data);
        }
        return file;
    }

}
