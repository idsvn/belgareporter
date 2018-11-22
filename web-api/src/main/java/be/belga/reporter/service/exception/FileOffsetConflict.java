package be.belga.reporter.service.exception;

public class FileOffsetConflict extends IllegalArgumentException {
    public FileOffsetConflict(String message) {
        super(message);
    }
}
