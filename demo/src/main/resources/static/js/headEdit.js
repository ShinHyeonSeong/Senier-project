const checkboxes = document.querySelectorAll('.inbox input[type="checkbox"]');
let lastChecked;

function handleCheck(e) {
    // check if they had the shift down
    // and check that they are checking it
    let inBetween = false;
    if (e.shiftKey && this.checked) {
        //go ahead and do what we please
        checkboxes.forEach(checkbox => {
            console.log(checkbox);
            if (checkbox === this || checkbox === lastChecked) {
                inBetween = !inBetween;
                console.log("starting to check them inbetween!");
            }
            if (inBetween) {
                checkbox.checked = true;
            }
        });
    }
    lastChecked = this;
}
checkboxes.forEach(checkbox => checkbox.addEventListener('click', handleCheck));