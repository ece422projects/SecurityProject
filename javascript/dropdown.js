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
  } else {
    x.style.display = "none";
  }
}

window.onclick = function(event) {
  if (!event.target.matches('#fileDropdown')) {
    var dropdowns = document.getElementById("fileDropdowns");
    dropdowns.style.display = "none";
  }
  if (!event.target.matches('#group')) {
    var dropdowns = document.getElementById("groups");
    dropdowns.style.display = "none";
  }
}
