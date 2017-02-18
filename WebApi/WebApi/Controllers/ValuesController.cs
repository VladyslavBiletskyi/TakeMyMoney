using Microsoft.AspNet.Identity;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Threading.Tasks;
using System.Web.Http;
using WebApi.Models;
using System.Data.Entity;

namespace WebApi.Controllers
{

    [Authorize]
    public class ValuesController : ApiController
    {
        TMMDbContext db = new TMMDbContext();
        [AllowAnonymous]
        [HttpGet]
        public IEnumerable<Performance> GetPerformances()
        {
            var Date = DateTime.Now.AddHours(-2);
            return db.Performances.Include(x => x.Image).Where(x => x.BeginingDateTime >= Date);
        }
        [HttpGet]
        public IEnumerable<Ticket> GetTickets()
        {
            string id = User.Identity.GetUserId();
            return db.Tickets.Where(x => x.UserId == id);
        }
        [HttpPost]
        public async Task<IHttpActionResult> BuyTicket()
        {
            string content = await Request.Content.ReadAsStringAsync();
            int id = JsonConvert.DeserializeObject<int>(content);
            Ticket ticket = null;
            Performance perf = db.Performances.Find(id);
            if (perf != null)
            {
                if (perf.TicketCount > perf.FacticalCount)
                {
                    IEnumerable<Ticket> tickets = db.Tickets.Where(x => x.PerformanceId == perf.Id).OrderBy(x => x.TicketNumber);
                    string UId = User.Identity.GetUserId();
                    int num = perf.FacticalCount + 1;
                    if (tickets.Count(x => x.TicketNumber == num) > 0)
                    {
                        int prevoius = 0;
                        foreach (Ticket element in tickets)
                        {
                            if (element.TicketNumber != prevoius + 1)
                            {
                                num = prevoius + 1;
                                break;
                            }
                            prevoius++;
                        }
                    }
                    ticket = new Ticket()
                    {
                        PerformanceId = perf.Id,
                        TicketNumber = num,
                        UserId = UId
                    };
                    db.Tickets.Add(ticket);
                    perf.FacticalCount++;
                    db.SaveChanges();
                    return Ok();
                }
                else
                {
                    return InternalServerError();
                }
            }
            else
            {
                return InternalServerError();
            }
        }
        [HttpPost]
        public async Task<TicketView> GetTicket()
        {
            TicketView ans = new TicketView();
            string content = await Request.Content.ReadAsStringAsync();
            int id = JsonConvert.DeserializeObject<int>(content);
            Ticket ticket = db.Tickets.Find(id);
            Performance perf = db.Performances.Find(ticket.PerformanceId);
            ans.Ticket = ticket;
            ans.Performance = perf;
            ans.Performance.Details = "";
            ans.Performance.Image = null;
            return ans;
        }
        [HttpPost]
        public async Task<IHttpActionResult> ReturnTicket()
        {
            string content = await Request.Content.ReadAsStringAsync();
            int id = JsonConvert.DeserializeObject<int>(content);
            Ticket ticket = db.Tickets.Find(id);
            string uId = User.Identity.GetUserId();
            if (ticket == null || ticket.UserId != uId)
            {
                return InternalServerError();
            }
            Performance perf = db.Performances.Find(ticket.PerformanceId);
            DateTime now = DateTime.Now.AddMinutes(30);
            if (perf == null || perf.BeginingDateTime < now)
            {
                return InternalServerError();
            }
            else
            {
                perf.FacticalCount--;
                db.Tickets.Remove(ticket);
                db.SaveChanges();
                return Ok();
            }
        }
        [HttpPost]
        public async Task<IHttpActionResult> CheckTicket()
        {
            string content = await Request.Content.ReadAsStringAsync();

            string query = content;//JsonConvert.DeserializeObject<string>(content);
            int tId = 0;
            int tNum = 0;
            string uId = "";
            int pId = 0;
            int currentpId = 0;            
            try
            {
                tId = int.Parse(query.Split(';')[0].Split(':')[1]);
                tNum= int.Parse(query.Split(';')[1].Split(':')[1]);
                uId= query.Split(';')[2].Split(':')[1];
                pId= int.Parse(query.Split(';')[3].Split(':')[1]);
                currentpId= int.Parse(query.Split(';')[4].Split(':')[1]);
            }
            catch (Exception e)
            {
                return InternalServerError(new Exception("Can't recognize data"));
            }
            Ticket ticket = db.Tickets.Find(tId);
            if (ticket == null || 
                ticket.UserId != uId ||
                ticket.TicketNumber!=tNum ||
                ticket.PerformanceId!=pId) //there is a real ticket
            {
                return InternalServerError(new Exception("Ticken don't exists"));
            }
            Performance perf = db.Performances.Find(currentpId);
            DateTime now = DateTime.Now.AddHours(-2);
            if (perf == null || perf.BeginingDateTime <= now || pId!=currentpId)//u can to come on the performance
            {
                return InternalServerError(new Exception("Ticken isn't right"));
            }
            else
            {
                return Ok();
            }
        }
    }
}
