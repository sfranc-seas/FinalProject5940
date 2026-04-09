package edu.upenn.cit5940.datamanagement;

import java.util.List;
import edu.upenn.cit5940.common.dto.Article;

public interface Parser {
    List<Article> parse(String filePath);
}
