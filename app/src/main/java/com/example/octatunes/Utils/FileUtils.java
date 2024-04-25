package com.example.octatunes.Utils;

import android.content.Context;
import android.content.res.Resources;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {

    public static File createFileFromRaw(Context context, int resourceId, String fileName) throws IOException {
        // Open the input stream using the resource ID
        InputStream inputStream = context.getResources().openRawResource(resourceId);

        // Create a new file in the app's internal storage directory
        File file = new File(context.getFilesDir(), fileName);

        // Write the contents of the input stream to the file
        OutputStream outputStream = new FileOutputStream(file);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }
        outputStream.close();
        inputStream.close();

        return file;
    }
}
