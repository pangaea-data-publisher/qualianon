package org.qualiservice.qualianon.model.project;

import java.io.*;
import java.nio.file.*;
import java.util.List;
import java.util.Map;
import java.util.zip.*;

public class FileZipper {

    private Map<String , List<File>> pathsToZip;
    private File projectDirectory;
    private File zipTargetDirectory;
    private String zippedProjectName;
    private String projectName;
    public static class Builder {
        private FileZipper zipper = new FileZipper();
        public Builder setPathsToZip(Map<String, List<File>> pathsToZip){
            zipper.pathsToZip = pathsToZip;
            return this;
        }
        public Builder setProjectDirectory(File projectDirectory){
            zipper.projectDirectory = projectDirectory;
            return this;
        }
        public Builder setZipTargetDirectory(File zipTargetDirectory){
            zipper.zipTargetDirectory = zipTargetDirectory;
            return this;
        }
        public Builder setProjectName(String projectName){
            zipper.projectName = projectName;
            return this;
        }
        public FileZipper build(){
            return zipper;
        }
    }

    public void exportProject() throws IOException {
        zipFiles(pathsToZip.get("anonymized"), "anonymized");
        zipFiles(pathsToZip.get("id"), "id");
    }

    private void zipFiles(List<File> pathsToZip, String zipName) throws IOException {
        this.zippedProjectName = String.join("_", projectName, zipName);
        String zippedProjectFullPath = appendFolder(zipTargetDirectory,zippedProjectName);
        try (
                FileOutputStream fos = new FileOutputStream(zippedProjectFullPath + ".zip");
                ZipOutputStream zos = new ZipOutputStream(fos);) {
            for (File file : pathsToZip) {
                addToZip(file, zos);
            }
        }
    }

    private void addToZip(File file, ZipOutputStream zos) throws IOException {
        if (file.isDirectory()) {
            String basePath = file.getParent();
            addDirectoryToZip(file, basePath, zos);
        }
    }

    private String getRelativePathInZip(File file, String basePath) {
        Path filePath = file.toPath();
        Path basePathObj = Paths.get(basePath);
        Path relativePathInZip =  basePathObj.relativize(filePath);
        relativePathInZip = Paths.get(zippedProjectName).resolve(relativePathInZip);
        return relativePathInZip.toString();
    }

    private String appendFolder(File parentDir,String folderToAppend){
        Path zipTargetPath = parentDir.toPath();
        Path newPath = zipTargetPath.resolve(folderToAppend);
        return newPath.toString();
    }

    private void addDirectoryToZip(File dir, String basePath, ZipOutputStream zos) throws IOException {
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                addDirectoryToZip(file, basePath, zos);
            } else {
                String relativePathInZip = getRelativePathInZip(file, basePath);
                //pack folders inside zip into parent directory named e.g. Project_anonymized or Project_id
                Path absolutePathOnDisk = file.toPath();
                zos.putNextEntry(new ZipEntry(relativePathInZip));
                Files.copy(absolutePathOnDisk, zos);
                zos.closeEntry();
            }
        }
    }
}
