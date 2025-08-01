package cn.idev.excel.metadata.data;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * comment
 *
 *
 */
@Getter
@Setter
@EqualsAndHashCode
public class CommentData extends ClientAnchorData {
    /**
     * Name of the original comment author
     */
    private String author;
    /**
     * rich text string
     */
    private RichTextStringData richTextStringData;
}
