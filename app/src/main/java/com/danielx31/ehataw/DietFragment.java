package com.danielx31.ehataw;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.plugins.RxJavaPlugins;


public class DietFragment extends Fragment {

    private BroadcastReceiver connectionReceiver;

    RecyclerView mRecyclerView;
    List<FoodData> foodDataList;
    FoodData mfoodData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_diet, container, false);

        RxJavaPlugins.setErrorHandler(e -> {
        });

        connectionReceiver = new ConnectionReceiver();

        mRecyclerView = view.findViewById(R.id.recycler_view);
//        GridLayoutManager gridLayoutManager = new GridLayoutManager(DietFragment.this, 1);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1);
        mRecyclerView.setLayoutManager(gridLayoutManager);

        SpacingItemDecoration spacingItemDecoration = new SpacingItemDecoration(10);
        mRecyclerView.addItemDecoration(spacingItemDecoration);

        foodDataList = new ArrayList<>();

        mfoodData = new FoodData("Vegetables Salad", "A salad is a dish consisting of mixed, mostly natural ingredients with at least one raw ingredient.\n" +
                "\n" +
                "★Why Vegetable Salad is Healthy?★\n" +
                "Leafy vegetables are a good choice for a healthful diet because they do not contain cholesterol and are naturally low in calories and sodium.\n" +
                "\n" +
                "★Ingredients★\n" +
                "•5 romaine lettuce leaves, torn into bite size pieces\n" +
                "•5 radishes, chopped\n" +
                "•2 fresh tomatoes, chopped\n" +
                "•2 green onion, minced\n" +
                "•1 small jicama, peeled and julienned\n" +
                "•1 cucumber, peeled and chopped\n" +
                "•1 red bell pepper, chopped\n" +
                "•2 tablespoons olive oil\n" +
                "•1 ½ tablespoons lemon juice\n" +
                "•1 tablespoon pomegranate juice\n" +
                "•1 ½ teaspoons water\n" +
                "•1 clove garlic, minced\n" +
                "•1 teaspoon chopped fresh dill\n" +
                "•1 teaspoon chopped fresh basil\n" +
                "•1 teaspoon salt\n" +
                "•1 teaspoon ground black pepper\n" +
                "\n" +
                "★Directions★\n" +
                "Step 1\n" +
                "Place lettuce, radishes, tomatoes, green onion, jicama, cucumber, and bell pepper in a large salad bowl.\n" +
                "\n" +
                "Step 2\n" +
                "Whisk olive oil, lemon juice, pomegranate juice, water, garlic, dill, basil, salt, and black pepper in a small bowl. Drizzle dressing over the salad just before serving.\n" +
                "\n" +
                "Image:loveandlemons.com\n" +
                "Reference: https://www.allrecipes.com/recipe/141314/the-best-vegetable-salad/\n" +"\n"+
                "https://extension.colostate.edu/topic-areas/nutrition-food-safety-health/health-benefits-and-safe-handling-of-salad-greens-9-373/#:~:text=Salad%20greens%20contain%20Vitamin%20A,low%20in%20calories%20and%20sodium.", "13 Calories, 2.8g Carbs, 0.1g Fats, 0.6g Protein", R.drawable.salad);
        foodDataList.add(mfoodData);

        mfoodData = new FoodData("Boiled Egg", "Boiled eggs are eggs, typically from a chicken, cooked with their shells unbroken, usually by immersion in boiling water.\n" +
                "\n" +
                "★Why Boiled Egg is Healthy?★\n" +
                "Hard-boiled eggs are an excellent source of lean protein. They'll fill you up without packing in too many calories, which is helpful if you want to lose weight. The protein in hard-boiled eggs also works alongside vitamin D to promote prenatal development.\n" +
                "\n" +
                "Instructions\n" +
                "Step 1\n" +
                "Place your eggs in a single layer on the bottom of your pot and cover with cold water. The water should be about an inch or so higher than the eggs. Cover the pot with a lid.\n" +
                "\n" +
                "Step 2\n" +
                "Over high heat, bring your eggs to a rolling boil.\n" +
                "\n" +
                "Step 3\n" +
                "Remove from heat and let stand in water for 10-12 minutes for large eggs. Reduce the time slightly for smaller eggs, and increase the standing time for extra-large eggs.\n" +
                "\n" +
                "Step 4\n" +
                "Drain water and immediately run cold water over eggs until cooled. Rapid cooling helps prevent a green ring from forming around the yolks.\n" +
                "\n" +
                "\n" +
                "Reference https://www.eggs.ca/eggs101/view/6/how-to-make-the-perfect-hard-boiled-egg\n" +
                "\n" +
                "https://www.webmd.com/diet/health-benefits-boiled-eggs\n" +
                "\n" +
                "Image: Serious Eats / Julia Estrada", "74 Calories, 1g Carbs, 5g Fats, 6g Protein", R.drawable.boiled_egg);
        foodDataList.add(mfoodData);

        mfoodData = new FoodData("Baked Salmon", "This is the very best, Easy Healthy Baked Salmon! Made with lemon and garlic for incredible flavor and baked in the oven for flaky tenderness, this tasty salmon recipe is the answer to busy nights and special occasions alike!\n" +
                "\n" +
                "★Why is eating salmon healthy?★\n" +
                "Salmon is high in protein and omega-3 fatty acids that provide well documented benefits for the heart and brain\n" +
                "\n" +
                "★Ingredients★\n" +
                "•Salmon Fillets\n" +
                "•Olive Oil \n" +
                "•Salt \n" +
                "•Cracked Black Pepper\n" +
                "•Minced Garlic\n" +
                "•Italian Herb Seasoning Blend\n" +
                "•Lemon\n" +
                "\n" +
                "Step By Step Directions\n" +
                "Step 1\n" +
                "Preheat the oven to 400 degrees and grease a large baking pan. Arrange the salmon fillets on the baking sheet and season generously with salt and pepper.\n" +
                "\n" +
                "Step 2\n" +
                "Stir together the olive oil, garlic, herbs, and juice of 1/2 of the lemon. Spoon this sauce over salmon fillets, being sure to rub all over the tops and sides of the salmon so it has no dry spots. Thinly slice the remaining 1/2 of the lemon and top each piece of salmon with a slice of lemon.\n" +
                "\n" +
                "Step 3\n" +
                "Bake the salmon in the oven for 15-18 minutes or until the salmon is opaque and flaky when pulled apart with a fork. You can broil the last 1-2 minutes if desired.\n" +
                "\n" +
                "Step 4\n" +
                "Garnish with fresh thyme or parsley if desired and serve.\n" +
                "\n" +
                "Source: https://www.lecremedelacrumb.com/best-easy-healthy-baked-salmon/\n" +
                "\n" +
                "https://doh.wa.gov/community-and-environment/food/fish/farmed-salmon", "55 Calories, 0g Carbs, 21g Fats, 38g Protein", R.drawable.bakedsalmon);
        foodDataList.add(mfoodData);

        mfoodData = new FoodData("Chicken Parm Casserole",
                "When you combine super crispy breaded chicken with rigatoni, warm marinara sauce, and loads of hot melted mozzarella and Parmesan cheese, good things happen.\n" +
                "\n" +
                "★Why is chicken Parm casserole good for you?★\n" +
                "contains high amounts of niacin, phosphorus, selenium, and protein. Niacin (Vitamin B3) can reduce LDL or the bad cholesterol and increase HDL or the good cholesterol. Selenium plays an important roll in thyroid health. Both selenium and niacin can reduce asthma symptoms.\n" +
                "\n" +
                "★Ingredients★\n" +
                "See recipe card at the bottom of post for full ingredient amounts and instructions\n" +
                "\n" +
                "•Rigatoni Pasta\n" +
                "•Marinara Sauce\n" +
                "•Mozzarella Cheese\n" +
                "•Parmesan Cheese\n" +
                "•Boneless Skinless Chicken Breasts\n" +
                "•Flour\n" +
                "•Seasoned Salt\n" +
                "•Eggs\n" +
                "•Breadcrumbs\n" +
                "•Vegetable Oil\n" +
                "•Butter\n" +
                "•Fresh Parsley\n" +
                "\n" +
                "How to Make it\n" +
                "BOIL THE RIGATONI.\n" +
                "Cook the rigatoni for 1 minute less than the al dente point. Drain.\n" +
                "\n" +
                "PREPARE THE CHICKEN.\n" +
                "Slice the chicken breast into thinner strips. Season it and coat it in flour, egg, and breadcrumbs. Fry it until golden brown. Slice into bite-sized pieces.\n" +
                "\n" +
                "ASSEMBLE THE CASSEROLE.\n" +
                "Combine the rigatoni and marinara sauce. Pour half into a 9 x 13 baking dish. Top with half of the chicken and cheese.\n" +
                "\n" +
                "Add the rest of the rigatoni. Top with remaining chicken and cheese.\n" +
                "\n" +
                "Cover and bake at 375° for 15 minutes, then uncover and bake for 25 minutes.\n" +
                "\n" +
                "Source: https://thecozycook.com/chicken-parmesan-casserole/\n" +
                "\n" +
                "https://www.aashpazi.com/chicken-parmesan-calories\n" +
                "\n", "361 Calories, 26g Carbs, 10g Fats, 32g Protein", R.drawable.chicken_parmesan_casserole);
        foodDataList.add(mfoodData);

        mfoodData = new FoodData("Mashed Potatoes", "homemade mashed potatoes are a classic holiday tradition. They’re a comforting reminder of the very best times gathered together around a table.\n" +
                "\n" +
                "★Why is mashed potato Healthy?★\n" +
                "They are low in fat, high in potassium and only have 150 calories per potato/serving. These are not empty calories either. There's actual great vitamins and nutrients in those calories. So, nutrition and calorie-wise, potatoes are great to add to your diet.\n" +
                "\n" +
                "★Ingredients★\n" +
                "•8 to 10 medium russet potatoes (about 3 lb.), peeled, cut into quarters\n" +
                "•1 teaspoon salt\n" +
                "•2 tablespoons butter, if desired\n" +
                "•Dash pepper\n" +
                "•1/4 cup hot milk\n" +
                "\n" +
                "Step 1\n" +
                "Place potatoes in large saucepan; add enough water to cover. Add 3/4 teaspoon of the salt. Bring to a boil. Reduce heat to medium-low; cover loosely and boil gently for 15 to 20 minutes or until potatoes break apart easily when pierced with fork. Drain well.\n" +
                "\n" +
                "Step 2\n" +
                "Return potatoes to saucepan; shake saucepan gently over low heat for 1 to 2 minutes to evaporate any excess moisture.\n" +
                "\n" +
                "Step 3\n" +
                "Mash potatoes with potato masher until no lumps remain. Add butter, pepper and remaining 1/4 teaspoon salt; continue mashing, gradually adding enough milk to make potatoes smooth and creamy.\n" +
                "\n" +
                "Source: https://www.pillsbury.com/recipes/easy-homemade-mashed-potatoes/09f0bba4-8bb9-4ce3-8d98-ae7a803eb7d5\n" +
                "\n" +
                "https://feelgoodfoodie.net/recipe/healthy-mashed-potatoes/#:~:text=They%20are%20low%20in%20fat,to%20add%20to%20your%20diet.", "88 Calories, 36.9g Carbs, 1.2g Fats, 4g Protein", R.drawable.mashed_potatoes);
        foodDataList.add(mfoodData);

        mfoodData = new FoodData("Pumpkin Soup", "Make the most of pumpkins in the supermarkets in the autumn and make this warming and filling pumpkin soup. Serve with our savoury granola sprinkled on top\n" +
                "\n" +
                "Why is Pumpkin Soup Healthy?\n" +
                "rich in several minerals and vitamins like copper, manganese, riboflavin, iron, niacin, and potassium as well as Vitamins C, K, and E. It is also extremely high in a powerful antioxidant called beta-carotene.\n" +
                "\n" +
                "Ingredients\n" +
                "1 pumpkin (around 1.5kg), peeled and chopped, seeds reserved\n" +
                "50g jumbo oats\n" +
                "2 tsp coriander seeds\n" +
                "1½ tsp chilli flakes\n" +
                "1 tsp tamari\n" +
                "1 tsp maple syrup\n" +
                "2 tbsp olive oil\n" +
                "1 onion, chopped\n" +
                "2 celery sticks, chopped\n" +
                "2 carrots, chopped\n" +
                "3 garlic cloves, sliced\n" +
                "1 litre vegetable stock\n" +
                "\n" +
                "Method\n" +
                "STEP 1\n" +
                "Put the pumpkin seeds in a sieve and rinse to remove any pulp. Spread out on a plate and pat dry. Toast 50g of the seeds in a pan over a medium heat along with the oats and 1 tsp each of the coriander seeds and chilli flakes for a few minutes until fragrant. Stir in the tamari and maple syrup. Simmer for 30 seconds until reduced and sticky. Transfer to a plate.\n" +
                "\n" +
                "STEP 2\n" +
                "Heat the oil in the pan over a medium heat and cook the onion, celery, carrots, garlic and pumpkin, along with the remaining spices and some seasoning for 10 mins. Pour in the stock. Bring to a simmer and cook with the lid on for 20-25 mins.\n" +
                "\n" +
                "STEP 3\n" +
                "Blitz until smooth using a hand blender. Serve with the savoury granola sprinkled on top.\n" +
                "\n" +
                "Source: https://www.bbcgoodfood.com/recipes/healthy-pumpkin-soup\n" +
                "\n" +
                "https://www.cult.fit/live/recipe/pumpkin-soup/RECIPE413#:~:text=Pumpkin%20soup%20is%20also%20rich,powerful%20antioxidant%20called%20beta%2Dcarotene.\n" +
                "\n" +
                "Image: BBC", " 71 Calories, 5.1g Carbs, 0.9g Fats, 1.3g Protein", R.drawable.pumpkinsoup);
        foodDataList.add(mfoodData);

        mfoodData = new FoodData("Pinto Beans", "The Pinto Bean is a speckled variety of common bean that is known for its creamy texture, mild flavor and ability to absorb flavors. Its Spanish name translates to \"speckled bean,\" referring to its mottled skin, which becomes uniform when cooked.\n" +
                "\n" +
                "Why is Pinto Beans Healthy?\n" +
                "Pinto beans are extremely nutritious. They're an excellent source of protein, fiber, vitamins, and minerals. These nutrients may provide several benefits, including improved blood sugar control and heart health. Pinto beans are also rich in various antioxidants and may help lower your risk of chronic disease\n" +
                "\n" +
                "Ingredients\n" +
                "\n" +
                "1 1/2 pounds pinto beans\n" +
                "2 teaspoons chili powder \n" +
                "1 teaspoon ground cumin \n" +
                "1/2 teaspoon paprika \n" +
                "1/4 teaspoon cayenne \n" +
                "3 thick-cut slices of bacon, cut into thirds \n" +
                "3 cloves garlic \n" +
                "2 bay leaves \n" +
                "1 medium onion, diced \n" +
                "1 red bell pepper, diced \n" +
                "Kosher salt and freshly ground black pepper\n" +
                "\n" +
                "Directions\n" +
                "Step 1\n" +
                "Put the beans in a large bowl, cover with cold water and let soak overnight.\n" +
                "\n" +
                "Step 2\n" +
                "Drain and rinse the beans, then put them in a large pot. Cover the beans with water and add the chili powder, cumin, paprika, cayenne, bacon, garlic, bay leaves, onion, bell pepper and some salt and pepper. Bring to a boil, then reduce the heat to low and cook until the beans are tender, 2 to 3 1/2 hours.\n" +
                "\n" +
                "Source: https://www.foodnetwork.com\n" +
                "\n" +
                "https://www.healthline.com/nutrition/pinto-beans-nutrition#:~:text=Pinto%20beans%20are%20extremely%20nutritious,your%20risk%20of%20chronic%20disease.", "245 Calories, 63g Carbs, 1.2g Fats, 21g Protein", R.drawable.pinto_beans);
        foodDataList.add(mfoodData);

        mfoodData = new FoodData("Creamy Yogart", "Homemade yogurt is a snap to make. All you really need is good quality milk, a few spoonfuls of your favorite plain yogurt to use as a starter culture, and some time to let it sit. Y\n" +
                "\n" +
                "Why is Creamy Yogurt Healthy?\n" +
                "Yogurts can be high in protein, calcium, vitamins, and live culture, or probiotics, which can enhance the gut microbiota. These can offer protection for bones and teeth and help prevent digestive problems. Low-fat yogurt can be a useful source of protein on a weight-loss diet. Probiotics may boost the immune system.\n" +
                "\n" +
                "INGREDIENTS\n" +
                "Yield: 1¾ quarts\n" +
                "2 quarts whole milk, the fresher the better\n" +
                "¼ cup heavy cream (optional)\n" +
                "3 to 4 tablespoons plain whole milk yogurt with live and active cultures\n" +
                "\n" +
                "PREPARATION\n" +
                "Step 1\n" +
                "Rub an ice cube over the inside bottom of a heavy pot to prevent scorching (or rinse the inside of the pot with cold water). Add milk and cream, if using, and bring to a bare simmer, until bubbles form around the edges, 180 to 200 degrees. Stir the milk occasionally as it heats.\n" +
                "\n" +
                "Step 2\n" +
                "Remove pot from heat and let cool until it feels pleasantly warm when you stick your pinkie in the milk for 10 seconds, 110 to 120 degrees. (If you think you’ll need to use the pot for something else, transfer the milk to a glass or ceramic bowl, or else you can let it sit in the pot.) If you’re in a hurry, you can fill your sink with ice water and let the pot of milk cool in the ice bath, stirring the milk frequently so it cools evenly.\n" +
                "\n" +
                "Step 3\n" +
                "Transfer ½ cup of warm milk to a small bowl and whisk in yogurt until smooth. Stir yogurt-milk mixture back into remaining pot of warm milk. Cover pot with a large lid. Keep pot warm by wrapping it in a large towel, or setting it on a heating pad, or moving to a warm place, such as your oven with the oven light turned on. Or just set it on top of your refrigerator, which tends to be both warm and out of the way.\n" +
                "\n" +
                "Step 4\n" +
                "Let yogurt sit for 6 to 12 hours, until the yogurt is thick and tangy; the longer it sits, the thicker and tangier it will become. (I usually let it sit for the full 12 hours.) Transfer the pot to the refrigerator and chill for at least another 4 hours; it will continue to thicken as it chills.\n" +
                "\n" +
                "Source: https://cooking.nytimes.com/recipes/1017991-creamy-homemade-yogurt\n" +
                "\n" +
                "https://www.medicalnewstoday.com/articles/295714", "59 Calories, 8g Carbs, 3g Fats, 3.4g Protein", R.drawable.yogart);
        foodDataList.add(mfoodData);

        mfoodData = new FoodData("Apples", "The apple is one of the pome (fleshy) fruits. Apples at harvest vary widely in size, shape, colour, and acidity, but most are fairly round and some shade of red or yellow. The thousands of varieties fall into three broad classes: cider, cooking, and dessert varieties."
                + "\n"+
                "\nWhy is Apple Healthy?\n"+
                "Apples are an incredibly nutritious fruit that offers multiple health benefits. They're rich in fiber and antioxidants. Eating them is linked to a lower risk of many chronic conditions, including diabetes, heart disease, and cancer. Apples may also promote weight loss and improve gut and brain health.\n"
                +"\n"+
                "Source: https://www.healthline.com/nutrition/10-health-benefits-of-apples#:~:text=Apples%20are%20an%20incredibly%20nutritious,improve%20gut%20and%20brain%20health.", "52 Calories, 13.8g Carbs, 0.2g Fats, 0.3g Protein", R.drawable.apples);
        foodDataList.add(mfoodData);

        mfoodData = new FoodData("Double Pecan Thumbprints", "These buttery frangipane-filled pecan buttons are decadent without being fragile, and they make excellent cookies for boxing up and giving as gifts. Dusting them with powdered sugar before baking creates a crackly, glossy coating. Feel free to add more afterward as well.\n" +
                "\n" +
                "Why Pecan Nuts is Healthy?\n" +
                "Pecans are a good source of calcium, magnesium, and potassium, which help lower blood pressure. Most of the fat found in pecans is a healthy type called monounsaturated fat. Eating foods with monounsaturated fat instead of foods high in saturated fats (like potato chips) can help lower levels of bad LDL cholesterol.\n" +
                "\n" +
                "Ingredients\n" +
                "FRANGIPANE\n" +
                "2 cups pecan halves, such as Fisher® Pecan Halves\n" +
                "⅓ cup granulated sugar\n" +
                "1 large egg white\n" +
                "2 Tbsp. unsalted butter, room temperature\n" +
                "2 tsp. espresso powder\n" +
                "½ tsp. kosher salt\n" +
                "¼ tsp. almond extract\n" +
                "\n" +
                "DOUGH AND ASSEMBLY\n" +
                "1¼ cups all-purpose flour\n" +
                "1 tsp. kosher salt\n" +
                "½ tsp. baking powder\n" +
                "¾ cup (1½ sticks) unsalted butter, room temperature\n" +
                "¼ cup granulated sugar\n" +
                "¾ cup powdered sugar, divided; plus more for serving (optional)\n" +
                "1 large egg yolk\n" +
                "1 tsp. vanilla extract or paste\n" +
                "\n" +
                "Preparation\n" +
                "FRANGIPANE\n" +
                "Step 1\n" +
                "Preheat oven to 350°. Lightly toast pecans on a rimmed baking sheet, tossing halfway through, until slightly darkened in color and fragrant, 6–8 minutes. Let cool; set 1¼ cups pecans aside for making the dough.\n" +
                "\n" +
                "Step 2\n" +
                "Pulse granulated sugar and remaining ¾ cup pecans in a food processor until nuts are very finely ground (be careful to stop before they become a paste), 30–60 seconds. Add egg white and pulse just to blend, then add butter, espresso powder, salt, and almond extract. Pulse just until mixture is smooth and combined. Scrape frangipane into a small bowl and chill at least 30 minutes before using.\n" +
                "\n" +
                "Step 3\n" +
                "Do Ahead: Frangipane can be made 3 days ahead. Cover and keep chilled. You will have more than you need, but try slathering leftovers on toasted brioche and baking until lightly browned (sort of like an almond croissant). We guarantee you won’t be upset about it.\n" +
                "\n" +
                "DOUGH AND ASSEMBLY\n" +
                "Step 4\n" +
                "Pulse flour, salt, baking powder, and 1 cup reserved pecans in clean food processor until nuts are very finely ground, about 1 minute.\n" +
                "\n" +
                "Step 5\n" +
                "Beat butter, granulated sugar, and ¼ cup powdered sugar in the bowl of a stand mixer fitted with paddle attachment on medium-high speed until light and fluffy, about 4 minutes. Add egg yolk and vanilla and beat until combined and no streaks remain. Reduce speed to low and add dry ingredients. Beat just until incorporated, about 1 minute. Cover bowl with plastic wrap and chill dough until it’s firm enough that you can scoop it and it will hold its shape, 30–45 minutes.\n" +
                "\n" +
                "Step 6\n" +
                "Place racks in upper and lower thirds of oven; preheat to 350°. Place ½ cup powdered sugar in a shallow bowl. Working in batches, scoop out tablespoonfuls of dough and roll into balls between your hands, then roll in powdered sugar, knocking off any excess. Transfer to 2 parchment-lined baking sheets as you work, spacing 2\" apart.\n" +
                "\n" +
                "Step 7\n" +
                "Bake cookies until puffed but edges are still soft, 6–8 minutes. Carefully remove from oven and make an indent in the center of each cookie with the handle end of a wooden spoon or a similar heatproof object. Spoon a heaping ½-teaspoonful of frangipane into each and top with a pecan half from remaining reserved ¼ cup. Return cookies to oven and continue to bake until edges are set and very lightly browned, 6–8 minutes longer. Let cool on baking sheets.\n" +
                "\n" +
                "Step 8\n" +
                "Just before serving, dust cookies with more powdered sugar if desired.\n" +
                "\n" +
                "Step 9\n" +
                "Do Ahead: Cookies can be baked 5 days ahead. Store airtight at room temperature.\n" +
                "\n" +
                "Source: https://www.bonappetit.com/recipe/double-pecan-thumbprints\n" +
                "\n" +
                "https://www.webmd.com/diet/health-benefits-pecans#:~:text=Pecans%20are%20a%20good%20source,levels%20of%20bad%20LDL%20cholesterol.", "120 Calories, 10g Carbs, 6g Fats, 1g Protein", R.drawable.double_pecan_thumbprints);
        foodDataList.add(mfoodData);


        MyAdapter myAdapter = new MyAdapter(getActivity(), foodDataList);
        mRecyclerView.setAdapter(myAdapter);



        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        getActivity().registerReceiver(connectionReceiver, filter);
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().unregisterReceiver(connectionReceiver);
    }
}