using System;

namespace TeamBot
{
#if WINDOWS || XBOX
    static class Program
    {
        /// <summary>
        /// The main entry point for the application.
        /// </summary>
        static void Main(string[] args)
        {
            using (TeamBot game = new TeamBot())
            {
                game.Run();
            }
        }
    }
#endif
}

