using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using WebApi.Models;

namespace WebApi.Controllers
{
    public class HomeController : Controller
    {
        public ActionResult Index()
        {
            ViewBag.Title = "Home Page";

            return View();
        }
        public ActionResult AddPerformance()
        {
            return View();
        }
        [HttpPost]
        public ActionResult AddPerformance(Performance perf) {
            try
            {
                Picture pic = new Picture();
                var p = Request.Files["Picture"];
                byte[] imageData = null;
                using (System.IO.BinaryReader br = new System.IO.BinaryReader(p.InputStream))
                {
                    imageData = br.ReadBytes(p.ContentLength);
                }
                TMMDbContext db = new TMMDbContext();
                if (imageData.Length != 0)
                {
                    pic.Name = p.FileName.Split('/').Last();
                    pic.Image = imageData;
                    db.Pictures.Add(pic);
                    db.SaveChanges();
                    perf.Image = pic;
                }
                db.Performances.Add(perf);
                db.SaveChanges();
                return RedirectToAction("Index", "Home");
            }
            catch (Exception e)
            {
                return RedirectToAction("PrintError", "Home", new {@error=e.GetType()+" "+e.StackTrace+" "+e.Source+" "+e.InnerException.Message});
            }
        }
        public string PrintError(string error)
        {
            return error;
        }
    }
}
