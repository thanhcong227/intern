package viettelsoftware.intern.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BorrowedBookInfo {

    private String bookTitle;
    private int quantity;
    private LocalDate borrowedDate;
    private LocalDate dueDate;
}
