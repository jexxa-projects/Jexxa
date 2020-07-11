package io.jexxa.tutorials.bookstore.domain.valueobject;

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
        this.prefix = prefix;
        this.registrationGroup = registrationGroup;
        this.registrant = registrant;
        this.publication = publication;
        this.checkDigit = checkDigit;
    }

    public String getISBNAsString()
    {
        var stream = new StringBuilder();
        stream.append(prefix.getValue())
                .append("-")
                .append(registrationGroup.getValue())
                .append("-")
                .append(registrant.getValue())
                .append("-")
                .append(publication.getValue())
                .append("-")
                .append(checkDigit.getValue());
        
        return stream.toString();
    }
}
