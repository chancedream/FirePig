package my.framework.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.util.FileCopyUtils;

public class SaveFile {
    private static final Logger logger = Logger.getLogger(SaveFile.class);
    private static final String fileDir = (String)ApplicationContext.getInstance().get("tempFileDir");
    
    public static void save(String fileName, String content) {
        try {
            FileCopyUtils.copy(content, new FileWriter(new File(fileDir, fileName)));
        } catch (IOException e) {
            logger.error("fail to save file " + fileName);
        }
    }
}
