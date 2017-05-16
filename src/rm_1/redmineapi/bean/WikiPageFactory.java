package rm_1.redmineapi.bean;

public class WikiPageFactory {
    public static WikiPage create(String title) {
        WikiPage page = new WikiPage();
        page.setTitle(title);
        return page;
    }
}
