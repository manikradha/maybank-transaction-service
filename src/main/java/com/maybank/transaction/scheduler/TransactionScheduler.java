package com.maybank.transaction.scheduler;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.*;

@Slf4j
@Component
@EnableScheduling
public class TransactionScheduler {

    @Value("${batch.input.dir:./input}")
    private String inputDir;

    @Value("${batch.processed.dir:./processed}")
    private String processedDir;

    @Value("${batch.failed.dir:./failed}")
    private String failedDir;

    @Autowired
    private Job job;

    @Autowired
    private JobLauncher jobLauncher;

    @PostConstruct
    public void init() {
        createDirIfNotExists(inputDir, "Input");
        createDirIfNotExists(processedDir, "Processed");
        createDirIfNotExists(failedDir, "Failed");
    }

    private void createDirIfNotExists(String dirPath, String dirName) {
        try {
            Path path = Paths.get(dirPath);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                log.info("Created {} directory: {}", dirName, path.toAbsolutePath());
            } else {
                log.debug("{} directory already exists: {}", dirName, path.toAbsolutePath());
            }
        } catch (Exception ex) {
            log.error("Failed to create {} directory '{}': {}", dirName, dirPath, ex.getMessage());
            throw new IllegalStateException("Directory initialization failed", ex);
        }
    }

    @Scheduled(fixedDelayString = "${batch.scan.delay-ms:30000}")
    public void scanInputDirectory() {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(inputDir), "*.txt")) {
            stream.forEach(this::processFileSafely);
        } catch (IOException ex) {
            log.error("Error scanning input directory: {}", ex.getMessage());
        }
    }

    private void processFileSafely(Path file) {
        try {
            if (!Files.exists(file) ||
                    !Files.isRegularFile(file) ||
                    !isFileStable(file)) {
                return;
            }

            JobParameters params = new JobParametersBuilder()
                    .addString("inputFile", file.toAbsolutePath().toString())
                    .addLong("startTime", System.currentTimeMillis())
                    .toJobParameters();

            JobExecution execution = jobLauncher.run(job, params);
            BatchStatus status = execution.getStatus();

            if (status == BatchStatus.COMPLETED || status == BatchStatus.FAILED) {
                archiveFile(file, status == BatchStatus.COMPLETED ? processedDir : failedDir);
            }

        } catch (Exception ex) {
            log.error("Failed processing {}: {}", file, ex.getMessage());
            archiveFile(file, failedDir);
        }
    }

    private boolean isFileStable(Path file) {
        try {
            long size = Files.size(file);
            Thread.sleep(2_000);
            return size == Files.size(file);
        } catch (IOException | InterruptedException ex) {
            log.warn("Stability check error for {}: {}", file, ex.getMessage());
            return false;
        }
    }

    private void archiveFile(Path source, String destDir) {
        try {
            Path targetDir = Paths.get(destDir);
            Files.createDirectories(targetDir);

            Path target = targetDir.resolve(source.getFileName());
            Files.move(source, target, StandardCopyOption.ATOMIC_MOVE);

            log.debug("Successfully moved file from {} to {}", source, target);
        } catch (IOException ex) {
            log.error("Failed to move file {}: {}", source, ex.getMessage());
            try {
                Path emergencyDir = Paths.get("./failed_emergency");
                Files.createDirectories(emergencyDir);
                Files.move(source, emergencyDir.resolve(source.getFileName()));
            } catch (IOException e) {
                log.error("Critical failure handling file {}: {}", source, e.getMessage());
            }
        }
    }
}
