package example.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Defines payload for Note create requests.")
public class CreateNoteRequest {
    @NotBlank
    @Size(max = 100)
    @Schema(example = "Sapienti sat")
    private String title;

    @NotBlank
    @Size(max = 255)
    @Schema(example = "Quod erat demonstrandum")
    private String content;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
