package viettelsoftware.intern.dto;

import lombok.Data;

import java.util.List;

@Data
public class PartialReturnRequest {
    private String borrowingId;
    private List<ReturnItem> returnedBooks;

    @Data
    public static class ReturnItem {
        private String bookId;
        private int quantity;
    }
}