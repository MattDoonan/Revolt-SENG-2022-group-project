package seng202.team3.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Basic step definitions for math operations as example cucumber tests
 * @author seng202 teaching team
 */
public class MathStepDefs {
    private int x;
    private int y;
    private int result;

    @Given("I have two numbers {int} and {int}")
    public void iHaveTwoNumbersAnd(Integer xIn, Integer yIn) {
        x = xIn;
        y = yIn;
    }

    @When("I multiply the two numbers")
    public void iMultiplyTheTwoNumbers() {
        result = x * y;
    }

    @When("I add the two numbers")
    public void iAddTheTwoNumbers() {
        result = x + y;
    }

    @Then("The result is {int}")
    public void theResultIs(Integer resultIn) {
        Assertions.assertEquals(result, resultIn);
    }

    @Test
    public void JamesTest() {
        assertTrue(true);
    }
}
