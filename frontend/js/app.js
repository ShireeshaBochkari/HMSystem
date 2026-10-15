// ---- protected routing ----
function route(){const h=location.hash;
  if(!auth.token)return authView(h==='#/register'?'register':'login');
  h==='#/rooms'?roomsView():studentsView()}
window.onhashchange=route;route();

function nav(){return`<header><b>🏠 Hostel Manager</b><nav><a href="#/students">Students</a><a href="#/rooms">Rooms</a></nav>
<span>${esc(auth.user)} <span class="badge">${auth.role}</span> <button class="s" onclick="logout()">Logout</button></span></header>`}
