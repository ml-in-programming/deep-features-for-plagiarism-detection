import java.util.LinkedHashMap;
import java.util.Map;

public class Implementation implements Interface {
    public Map<String, Object> getValues(boolean deep) {
        Map<String, Object> result = new LinkedHashMap<String, Object>();

        Interface defaults = getDefaultSection();

        if (defaults != null) {
            result.putAll(defaults.getValues(deep));
        }

        return result;
    }

    public Interface getDefaultSection() {
        return new Implementation();
    }
}
