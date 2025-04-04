package viettelsoftware.intern.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BorrowedBookInfo {

    private String bookTitle;
    private int quantity;
}
