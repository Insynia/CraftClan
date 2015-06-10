package fr.insynia.craftclan;

import com.earth2me.essentials.api.NoLoanPermittedException;
import com.earth2me.essentials.api.UserDoesNotExistException;

import java.math.BigDecimal;

import static com.earth2me.essentials.api.Economy.add;
import static com.earth2me.essentials.api.Economy.hasEnough;
import static com.earth2me.essentials.api.Economy.substract;

/**
 * For CraftClan
 * Created by Doc on 10/06/15 at 18:56.
 */
public class EconomyCC {
    public static boolean has(String name, BigDecimal amount) {
        try {
            return hasEnough(name, amount);
        } catch (UserDoesNotExistException e) {
            return false;
        }
    }

    public static boolean give(String name, BigDecimal amount) {
        try {
            add(name, amount);
            return true;
        } catch (NoLoanPermittedException e) {
            return false;
        } catch (UserDoesNotExistException e) {
            return false;
        }
    }

    public static boolean take(String name, BigDecimal amount) {
        try {
            substract(name, amount);
            return true;
        } catch (UserDoesNotExistException e) {
            return false;
        } catch (NoLoanPermittedException e) {
            return false;
        }
    }
}
