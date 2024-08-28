const SERVER_URL = "https://apdc-2024-individual-418722.oa.r.appspot.com";
// const SERVER_URL = "http://localhost:8080";

toastr.options = {
    "closeButton": true,
    "debug": false,
    "newestOnTop": false,
    "progressBar": true,
    "positionClass": "toast-bottom-full-width",
    "preventDuplicates": true,
    "onclick": null,
    "showDuration": "300",
    "hideDuration": "1000",
    "timeOut": "12000",
    "extendedTimeOut": "6000",
    "showEasing": "swing",
    "hideEasing": "linear",
    "showMethod": "fadeIn",
    "hideMethod": "fadeOut"
};

let logoutForm = document.getElementById("lnk-logout");
logoutForm.addEventListener("click", function (e) {
    e.preventDefault();

    const headers = new Headers();
    headers.append("Cookie", localStorage.getItem("token"));

    fetch(SERVER_URL + "/api/logout", {
        method: "POST",
        headers: headers
    })
        .then(response => {
            if (response.ok) {
                localStorage.clear();
                document.getElementById("lnk-logout").hidden = true;
                localStorage.setItem("logout", "\"Logged out successfully!\"");
                window.location.href = "home.html";
            } else {
                toastr.error("Logout failed!");
            }
        })
});