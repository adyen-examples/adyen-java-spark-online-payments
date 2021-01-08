package view;

import java.util.Map;
import com.hubspot.jinjava.JinjavaConfig;

import spark.ModelAndView;
import spark.template.jinjava.JinjavaEngine;


public class RenderUtil {

    public static String render(Map<String, Object> model, String templatePath) {
        JinjavaConfig config = new JinjavaConfig();
        CustomResourceLocator customResourceLocator = new CustomResourceLocator();

        return new JinjavaEngine(config, customResourceLocator).render(new ModelAndView(model, templatePath));
    }
}
