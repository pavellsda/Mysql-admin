package com.pavellsda.mysqladmin.utils;

import javafx.scene.control.TextField;

/**
 * Created by pavellsda on 28.01.17.
 */
public class NumTextField extends TextField {

    private String type = null;

    public NumTextField(String type){this.type = type;}
    public NumTextField(){};

    @Override
    public void replaceText(int start, int end, String text) {
        if (validate(text))
        {
            super.replaceText(start, end, text);
        }
    }

    @Override
    public void replaceSelection(String text) {
        if (validate(text))
        {
            super.replaceSelection(text);
        }
    }

    private boolean validate(String text) {

        return text.matches("[0-9]*");
    }

    public void setType(String type){this.type = type;}

    public boolean testType(String text){
        try {
            long currentNum = Integer.parseInt(text);
            switch (type) {
                case "INT":
                    return (currentNum > Integer.MIN_VALUE && currentNum < Integer.MAX_VALUE);

                case "INT UNSIGNED":
                    return (currentNum > 0 && currentNum < 4294967295L);

                case "MEDIUMINT":
                    return (currentNum > -8388608 && currentNum < 8388608);

                case "MEDIUMINT UNSIGNED":
                    return (currentNum > 0 && currentNum < 16777215);

                case "BIGINT":
                    return (currentNum > Math.pow(-2, 63) && currentNum < Math.pow(2, 63) - 1);

                case "BIGINT UNSIGNED":
                    return (currentNum > 0 && currentNum < Math.pow(2, 64));

                case "SMALLINT":
                    return (currentNum > -32768 && currentNum < 32767);

                case "SMALLINT UNSIGNED":
                    return (currentNum > 0 && currentNum < 65535);

                case "TYNYINT":
                    return (currentNum > -128 && currentNum < 127);

                case "TYNYINT UNSIGNED":
                    return (currentNum > 0 && currentNum < 255);

            }
        } catch (NumberFormatException ex){
            System.out.println("Need number");
        }
        return text.matches("[0-9]*");
    }
}

