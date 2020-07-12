package io.jexxa.tutorials.bookstore.domain.valueobject;

import java.util.Objects;

import org.apache.commons.lang3.Validate;

/**
 * Prefix element – currently this can only be either 978 or 979. It is always 3 digits in length
 * Registration group element – this identifies the particular country, geographical region, or language area participating in the ISBN system. This element may be between 1 and 5 digits in length
 * Registrant element - this identifies the particular publisher or imprint. This may be up to 7 digits in length
 * Publication element – this identifies the particular edition and format of a specific title. This may be up to 6 digits in length
 * Check digit – this is always the final single digit that mathematically validates the rest of the number. It is calculated using a Modulus 10 system with alternate weights of 1 and 3.
 */
public class ISBN13
{
    private final Prefix prefix;
    private final RegistrationGroup registrationGroup;
    private final Registrant registrant;
    private final Publication publication;
    private final CheckDigit checkDigit;

    public ISBN13(Prefix prefix, RegistrationGroup registrationGroup, Registrant registrant, Publication publication, CheckDigit checkDigit)
    {
        Validate.notNull(prefix);
        Validate.notNull(registrationGroup);
        Validate.notNull(registrant);
        Validate.notNull(publication);
        Validate.notNull(checkDigit);

        this.prefix = prefix;
        this.registrationGroup = registrationGroup;
        this.registrant = registrant;
        this.publication = publication;
        this.checkDigit = checkDigit;

        validateISBN();
    }

    public Prefix getPrefix()
    {
        return prefix;
    }

    public RegistrationGroup getRegistrationGroup()
    {
        return registrationGroup;
    }

    public Registrant getRegistrant()
    {
        return registrant;
    }

    public Publication getPublication()
    {
        return publication;
    }

    public CheckDigit getCheckDigit()
    {
        return checkDigit;
    }

    public String toPrettyString()
    {
        return prefix.getValue() +
                "-" +
                registrationGroup.getValue() +
                "-" +
                registrant.getValue() +
                "-" +
                publication.getValue() +
                "-" +
                checkDigit.getValue();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }
        ISBN13 isbn13 = (ISBN13) o;
        return prefix.equals(isbn13.prefix) &&
                registrationGroup.equals(isbn13.registrationGroup) &&
                registrant.equals(isbn13.registrant) &&
                publication.equals(isbn13.publication) &&
                checkDigit.equals(isbn13.checkDigit);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(prefix, registrationGroup, registrant, publication, checkDigit);
    }

    private char[] getDigits()
    {
        var stream = new StringBuilder();
        stream.append(prefix.getValue())
                .append(registrationGroup.getValue())
                .append(registrant.getValue())
                .append(publication.getValue());
        return stream.toString().toCharArray();
    }

    private void validateISBN()
    {
        var digits = getDigits();

        int digitSum = 0;
        for (int i = 0; i < digits.length; ++i)
        {
            if ( i % 2 == 0)
            {
                digitSum += digits[i];
            }
            else
            {
                digitSum += digits[i] * 3;
            }
        }

        var calculatedCheckDigit = (( 10 - digitSum ) % 10) % 10;

        Validate.isTrue( calculatedCheckDigit == checkDigit.getValue(), "Invalid checksum: Expected value is " + calculatedCheckDigit + " Given value is " + checkDigit.getValue());
    }
}
