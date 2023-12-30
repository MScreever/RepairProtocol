package com.saxion.repairprotocol.FileSearch;

import java.io.File;

public class DirectoryDeletion
{
    public static void deleteDirectory(File directoryToBeDeleted)
    {
        if (directoryToBeDeleted.isDirectory())
        {
            File[] files = directoryToBeDeleted.listFiles();

            if (files == null) return;

            for (File file : files)
            {
                deleteDirectory(file);
            }
        }

        if (!directoryToBeDeleted.delete())
        {
            System.out.println("Failed to delete directory: " + directoryToBeDeleted);
        }
    }
}
