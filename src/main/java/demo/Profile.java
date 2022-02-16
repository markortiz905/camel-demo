package demo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Profile {
    private String username;
    private int age;
    private double height;//in inches eg. 5.5
    private double weight;//in kilograms
    private double bmi;
}
