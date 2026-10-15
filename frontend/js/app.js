// ---- protected routing ----
function route(){const h=location.hash;
  if(!auth.token)return authView(h==='#/register'?'register':'login');
  if(h==='#/students'&&isOwner())return studentsView();
  if(h==='#/rooms')return roomsView();
  const section=(h||'#/dashboard').replace('#/','');
  if(['dashboard','messages','fees','meals','complaints'].includes(section))return portalView(section);
  location.hash='#/dashboard'}
window.onhashchange=route;route();

function nav(){const links=isOwner()?[
  ['dashboard','Overview'],['students','Hostelers'],['rooms','Rooms'],['messages','Messages'],['fees','Fees'],['meals','Meals'],['complaints','Complaints']
]:[['dashboard','Home'],['messages','Messages'],['fees','Fees'],['meals','Meals'],['complaints','Complaints']];
  return`<header><b>Hostel Manager</b><nav>${links.map(([path,label])=>`<a href="#/${path}">${label}</a>`).join('')}</nav>
  <span>${esc(auth.user)} <span class="badge">${isOwner()?'OWNER':'HOSTELER'}</span> <button class="s" onclick="logout()">Logout</button></span></header>`}
