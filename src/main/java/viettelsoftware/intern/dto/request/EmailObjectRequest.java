package viettelsoftware.intern.dto.request;

import lombok.*;

import java.io.File;
import java.util.Arrays;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmailObjectRequest {
    String[] emailTo;
    String[] emailCC;
    String[] emailBCC;
    String subject;
    String template;
    Map<String, Object> params;
    File[] files;
    String[] fileNames;

    @Override
    public String toString() {
        return "EmailObjectRequest{" +
                "emailTo=" + Arrays.toString(emailTo) +
                ", emailCC=" + Arrays.toString(emailCC) +
                ", emailBCC=" + Arrays.toString(emailBCC) +
                ", subject='" + subject + '\'' +
                ", template='" + template + '\'' +
                ", params=" + params +
                ", files=" + Arrays.toString(files) +
                ", fileNames=" + Arrays.toString(fileNames) +
                '}';
    }
}