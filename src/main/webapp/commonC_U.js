function validateEmail(email) {
    const re = /\S+@\S+\.\S+/;
    return re.test(email);
}

function validatePhoneNr(phoneNr) {
    const re = /^\+?\d{9,15}$/;
    return re.test(phoneNr);
}

function validateZipCode(zipCode) {
    const re = /^\d{4}-\d{3}$/;
    return re.test(zipCode);
}

function validateNif(nif) {
    const re = /^\d{9}$/;
    return re.test(nif);
}