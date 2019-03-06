function groupDropdown() {
  var x = document.getElementById("groups");
  if (x.style.display === "none") {
    x.style.display = "block";
  } else {
    x.style.display = "none";
  }
}

function fileDropdown() {
  var x = document.getElementById("fileDropdowns");
  if (x.style.display === "none") {
    x.style.display = "block";
    console.log("Opening the file dropdown");
  } else {
    x.style.display = "none";
  }
}

window.onclick = function(event) {

}
