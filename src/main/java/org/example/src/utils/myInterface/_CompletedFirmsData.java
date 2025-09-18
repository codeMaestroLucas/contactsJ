package org.example.src.utils.myInterface;

import lombok.Getter;
import org.example.src.entities.BaseSites.Site;
import org.example.src.sites.byNewPage.*;
import org.example.src.sites.byPage.*;

@Getter
public class _CompletedFirmsData {

    public final static Site[] byPage = {
            new AlTamimi(), new ApplebyGlobal(), new ArnoldAndPorter(), new BNT(), new WilliamFry(),
            new AronTadmorLevy(), new Ashurst(), new BonelliErede(), new Walkers(), new ByrneWallace(),
            new CampbellsLegal(), new CareyOlsen(), new ClemensLaw(), new CollasCrill(), new ConsortiumLegal(),
            new Conyers(), new DahlLaw(), new DechertLLP(), new DillonEustace(), new DittmarAndIndrenius(),
            new Foyen(), new GianniAndOrigoni(), new Blakes(), new StikemanElliott(), new Cassels(),
            new McCarthyTetrault(), new GreenbergTraurig(), new PeterAndKim(), new BennettJones(), new RitchMuellerAndNicolau(),
            new BLGLaw(), new HillDickinson(), new HakunLaw(), new HannesSnellman(), new HavelPartners(),
            new SpencerWest(), new CassidyLevyKent(), new HowseWilliams(), new JonesDay(), new Kinstellar(),
            new Krogerus(), new KromannReumert(), new LathamAndWatkins(), new LEXLogmannsstofa(),
            new MagnussonLaw(), new Matheson(), new MeitarLaw(), new RelianceCorporateAdvisors(),
            new Ogier(), new DuncanCotterill(), new Pedersoli(), new RopesAndGray(), new Schoenherr(),
            new WatsonFarleyAndWilliams(), new ShinAndKim(), new SprusonAndFerguson(), new TaylorWessing(), new WhiteAndCase(),
            new SEUM(), new WhiteAndCase(), new WinstonAndStrawn(), new WolfTheiss(), new TucaZbarcea(),
            new PanettaConsultingGroup(), new Finreg360(), new CrowellAndMoring(), new HFW(), new Njord(),
            new Titov(), new VBAdvocates(), new NPPLegal(), new GomezAceboAndPombo(), new WildeboerDellelce(),
            new HNA(), new JGSA(), new RDJ(), new MijaresAngoitiaCortesAndFuentes(), new GarciaBodan(),
            new JamesAndWells(), new BankiHaddockFiora(), new MBIP(), new LEFOSSE(), new Andersen(),
            new SantamarinaAndSteta(), new AguayoEcclefieldAndMartinez(), new Tavares(), new DaleAndLessmann(), new OyenWiggs(),
            new NelliganLaw(), new ClarkWilson(), new RamdasAndWong(), new StephensonHarwood(), new SFKSLaw(),
            new KISCHIP(), new VOPatentsAndTrademarks(), new ZamfirescuRacotiPredoiu(), new LaszczukAndWspolnicy(), new DrzewieckiTomaszek(),
            new MSP(), new VieringJentschuraAndPartner(), new Esche(), new Dompatent(), new BARDEHLEPAGENBERG(),
            new Kolster(), new HammarskioldAndCo(), new AWA(), new ArnesenIP(), new Aera(),
            new HerbertSmithFreehillsKramer(), new BarristonLaw(), new Chattertons(), new BlandyAndBlandy(), new Allens(),
            new DBHLaw(), new Helmsman(), new DRAndAJU(), new TahotaLaw(), new StewartMcKelvey(),
            new SIRIUS(), new Roschier(), new Liedekerke(), new CliffordChance(), new MorganLewis(),
            new SheppardMullin(), new Skadden(), new McMillan(), new SteinmetzHaringGurman(), new FluegelPreissner(),

            /* Await firms - Countries to avoid for now ...
            new BaeKimAndLee(),
            new CFNLaw(),
            new Deacons(),
            new MalleyAndCo(),
            new TiruchelvamAssociates(),
            new MinterEllisonRuddWatts(),
            new MacphersonKelley(),
            new DCCLaw(),
            new MASLaw(),
            new K1Chamber(),
            new ZhongziLaw(),
            new GrandwayLaw(),
            new LeeAndKo(),
            new MZMLegal(),
            */

            /** Indian Firms
            new VeritasLegal(), new ALMTLegal(), new AnandAndAnand(),

             * Russia Firms
            new MellingVoitishkinAndPartners()

             * No more lawyers to register
            new FoxAndMandal(),

             * Problems
             *
            */
    };



    public final static Site[] byNewPage = {
            new ALGoodbody(), new Frontier(), new Oxera(), new GuantaoLaw(), new Sangra(),
            new Onsagers(), new BWBLLP(), new BCFLaw(), new ThompsonDorfmanSweatman(), new Dottir(),
            new OgletreeDeakins(), new EBN(), new Ellex(), new ControlRisks(), new ArthurCox(),
            new AsafoAndCo(), new BarneaAndCo(), new Beauchamps(), new Borenius(), new BrigrardUrrutia(),
            new CarneluttiLaw(), new Cobalt(), new Dentons(), new TEMPLARS(), new Madrona(),
            new Sherrards(), new EllisonsSolicitors(),
            new Gadens(), new DaviesWardPhillipsAndVineberg(), new GittiAndPartners(), new GORG(), new GornitzkyAndCo(),
            new HiggsAndJohnson(), new Horten(), new HuiyeLaw(), new LewissSilkin(), new IbanezParkman(),
            new KuriBrena(), new NaderHayauxAndGoebel(), new Baumgartners(), new PrasadAndCompany(), new ENSAfrica(),
            new OneEssexCourt(), new YoungList(), new LangsethAdvokat(), new LatamLex(), new Legalis(),
            new Legance(), new LexCaribbean(), new MeyerKoring(), new Mourant(), new KeystoneLaw(),
            new KRBLaw(), new Kvale(), new Werksmans(), new DGKV(), new Fischer(),
            new MyersFletcherAndGordon(), new RemfryAndSagar(), new WikborgRein(), new Noerr(), new PaviaAndAnsaldo(),
            new PearlCohen(), new PortolanoCavallo(), new Pulegal(), new Sorainen(), new Thommessen(),
            new Wiersholm(), new AOil(), new Belgravia(), new EProint(), new FCMLimited(), new FPSLaw(),
            new Ferrere(), new HamsoPatentybra(), new Holmes(), new JacksonEttiAndEdu(), new Kondrat(),
            new LatinAlliance(), new LePooleBekema(), new SabaAndCo(), new SargentAndKrahn(), new WardynskiAndPartners(),
            new DumontBergmanBider(), new MBM(), new MarksAndClerk(), new HansOffiaAndAssociates(), new PortaAndConsulentiAssociati(),
            new EPAndC(), new Hamso(), new JWP(), new ACAndR(), new KennedyVanderLaan(),
            new METIDA(), new PhilippeAndPartners(), new SBGK(), new DANUBIAPatentAndLaw(), new ReinhardSkuhraWeiseAndPartnerGbR(),
            new PrinzAndPartner(), new PrueferAndPartner(),

            /* Await firms - Countries to avoid for now ...
            new NurmansyahAndMuzdalifah(),
            new TompkinsWake(),
            new FangdaPartners(),
            new LonganLaw(),
            new MishconKaras(),
            new TCLaw(),
            new DeutschMiller(),
            new CovenantChambers(),
            new DWFoxTucker(),
            new HYLeungAndCo(),
            */

            // TEST IT
//        new Szecskay(), new TannerDeWitt(), new ZeposAndYannopoulos(),


            /**
             * INDIAN
             * - completed
             * new DSKLegal(),
             *
             * - to complete
             * new HSAAdvocates(), new SamvadPartners(), new JSA()
             * new Poulschmith(), new SimmonsAndSimmons(), new SimmonsWolfhagen(),
             *
            */
    };




    public final static Site[] byFilter = {

    };




    public final static Site[] byClick = {

    };
}